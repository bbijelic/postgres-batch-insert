package com.github.bbijelic.pbi.entity;

import javax.persistence.*;

/**
 * TestTable entity
 */
@Entity
@Table(name = "test_table")
public class TestTable {

    @Id
    @GeneratedValue(generator = "test_table_seq_generator", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "test_table_seq_generator", allocationSize = 1000, sequenceName = "test_table_seq")
    private Long id;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @Column(name = "data", insertable = true, updatable = true, nullable = false)
    private String data;

    public String getData(){
        return data;
    }

    public void setData(String data){
        this.data = data;
    }

    public TestTable(final String data){
        this.data = data;
    }

    @Override
    public String toString(){
        final StringBuffer sb = new StringBuffer("TestTable{");
        sb.append("id=").append(id);
        sb.append(", data='").append(data).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
