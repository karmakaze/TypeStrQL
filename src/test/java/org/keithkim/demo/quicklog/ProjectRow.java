package org.keithkim.demo.quicklog;

public class ProjectRow {
    public long id;
    public long accountId;
    public String name;
    public String domain;

    public ProjectRow(long id, long accountId, String name, String domain) {
        this.id = id;
        this.accountId = accountId;
        this.name = name;
        this.domain = domain;
    }

    @Override
    public String toString() {
        return "ProjectRow<" + intoString(new StringBuilder()) + ">";
    }

    public StringBuilder intoString(StringBuilder buffer) {
        buffer.append(String.format("id:%d, accountId:%d, name:%s, domain:%s",
                id, accountId, name, domain));
        return buffer;
    }
}
