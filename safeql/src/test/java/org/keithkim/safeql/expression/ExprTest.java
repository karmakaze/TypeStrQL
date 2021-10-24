package org.keithkim.safeql.expression;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.*;
import static org.keithkim.safeql.test.TestHelpers.assertMatches;

class ExprTest {
    @Test
    void null_equals_returnsFalse() {
        Expr<String> a = new Expr<>("SELECT * FROM account");

        assertFalse(a.equals(null));
    }

    @Test
    void incompatibleType_equals_returnsFalse() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        String unrelated = "SELECT * FROM account";

        assertFalse(a.equals(unrelated));
    }

    @Test
    void sameButDistinctIdentity_equals_returnsTrue() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>(new StringBuilder("SELECT *").append(" FROM account").toString());

        assertEquals(a, b);
    }

    @Test
    void identicallyConstructed_equals_returnsTrue() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>("SELECT * FROM account");

        assertEquals(a, b);
    }

    @Test
    void different_equals_returnsFalse() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>("SELECT * FROM project");

        assertNotEquals(a, b);
    }

    @Test
    void sameButDistinctIdentity_hashCode_match() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>(new StringBuilder("SELECT *").append(" FROM account").toString());

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void identicallyConstructed_hashCode_match() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>("SELECT * FROM account");

        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void different_hashCode_dontMatch() {
        Expr<String> a = new Expr<>("SELECT * FROM account");
        Expr<String> b = new Expr<>("SELECT * FROM project");

        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void nobinds_sql_returnsLiteral() {
        Expr<String> subject = new Expr<>("SELECT * FROM account");

        assertEquals("SELECT * FROM account", subject.sql());
    }

    @Test
    void withBinds_sql_returnsLiteral() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset");
        subject.bind("min_id", 1000);
        subject.bind("max_id", 2000);
        subject.bind("limit", 10);
        subject.bind("offset", 5);

        assertEquals("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset", subject.sql());
    }

    @Test
    void nobinds_binds_returnsEmptyMap() {
        Expr<String> subject = new Expr<>("SELECT * FROM account");

        assertEquals(emptyMap(), subject.binds());
    }

    @Test
    void withMultiplebinds_binds_returnsSortedMapWithAllbinds() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset");
        subject.bind("min_id", 1000);
        subject.bind("max_id", 2000);
        subject.bind("limit", 10);
        subject.bind("offset", 5);

        assertTrue(subject.binds() instanceof SortedMap);
        assertEquals(ImmutableMap.of("min_id", 1000, "max_id", 2000, "limit", 10, "offset", 5), subject.binds());
    }

    @Test
    void withLocalBinds_sql_shouldUseNumberedNames() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset");
        subject.bindLocal("min_id", 1000);
        subject.bindLocal("max_id", 2000);
        subject.bindLocal("limit", 10);
        subject.bindLocal("offset", 5);

        Pattern pattern = Pattern.compile("SELECT \\* FROM account WHERE id BETWEEN :(min_id_[0-9]+) AND :(max_id_[0-9]+) LIMIT :(limit_[0-9]+) OFFSET :(offset_[0-9]+)");
        assertMatches(pattern, subject.sql());

        Set<String> numberedNames = new HashSet<>();
        Matcher matcher = pattern.matcher(subject.sql());
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                numberedNames.add(matcher.group(i));
            }
        }
        assertEquals(numberedNames, subject.localBinds().keySet());
    }

    @Test
    void withBindsAndLocalBinds_bind_shouldNotChangeLocalBinds() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset");
        subject.bindLocal("min_id", 1000);
        subject.bindLocal("max_id", 2000);
        subject.bind("min_id", 3000);
        subject.bind("max_id", 4000);
        subject.bind("limit", 10);
        subject.bind("offset", 5);

        assertEquals(ImmutableMap.of("limit", 10, "offset", 5), subject.binds());

        Pattern pattern = Pattern.compile("SELECT \\* FROM account WHERE id BETWEEN :(min_id_[0-9]+) AND :(max_id_[0-9]+) LIMIT :limit OFFSET :offset");
        assertMatches(pattern, subject.sql());

        SortedSet<String> numberedNames = new TreeSet<>();
        Matcher matcher = pattern.matcher(subject.sql());
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                numberedNames.add(matcher.group(i));
            }
        }
        assertEquals(numberedNames, subject.localBinds().keySet());

        assertTrue(numberedNames.first().startsWith("max_id_"));
        assertTrue(numberedNames.last().startsWith("min_id_"));
        assertEquals(ImmutableMap.of(numberedNames.first(), 2000, numberedNames.last(), 1000), subject.localBinds());
    }

    @Test
    void bindLocalPositional_shouldSetNamedParam() {
        Expr<String> subject = Expr.expr("SELECT * FROM account WHERE id = ?", 1000);
        String varName = "_1_" + subject.objectId;

        assertEquals("<SQL: SELECT * FROM account WHERE id = :"+varName+"; BIND: "+varName+":1000>", subject.toString());
    }

    @Test
    void bindLocalPositionals_shouldSetNamedParams() {
        Expr<String> subject = Expr.expr("SELECT * FROM point WHERE (x, y) IN ((?, ?), (?, ?))",
                1, 2, 3, 4);
        Map<String, Object> binds = new LinkedHashMap<>(4);
        for (int i = 1; i <= 4; i++) {
            binds.put("_"+ i +"_" + subject.objectId, i);
        }

        String sql = String.format("SELECT * FROM point WHERE (x, y) IN ((:%s, :%s), (:%s, :%s))", binds.keySet().toArray());
        String bind = Joiner.on(", ").withKeyValueSeparator(":").join(binds);
        assertEquals("<SQL: "+ sql +"; BIND: "+bind+">", subject.toString());
    }

    @Test
    void nobinds_toString_shouldFormatInAngleQuotes() {
        Expr<String> subject = new Expr<>("SELECT * FROM account");

        assertEquals("<SQL: SELECT * FROM account;>", subject.toString());
    }

    @Test
    void withOneBinding_toString_shouldFormatInAngleQuotes() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id >= :min_id");
        subject.bind("min_id", 1000);

        assertEquals("<SQL: SELECT * FROM account WHERE id >= :min_id; BIND: min_id:1000>", subject.toString());
    }

    @Test
    void withMultiplebinds_toString_shouldFormatSortedInAngleQuotes() {
        Expr<String> subject = new Expr<>("SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset");
        subject.bind("min_id", 1000);
        subject.bind("max_id", 2000);
        subject.bind("limit", 10);
        subject.bind("offset", 5);

        assertEquals("<SQL: SELECT * FROM account WHERE id BETWEEN :min_id AND :max_id LIMIT :limit OFFSET :offset;"+
                " BIND: limit:10, max_id:2000, min_id:1000, offset:5>", subject.toString());
    }
}
