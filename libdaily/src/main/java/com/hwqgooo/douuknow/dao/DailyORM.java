package com.hwqgooo.douuknow.dao;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Schema;

public class DailyORM {
    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.hwqgooo.douknow.model.dao");
        addDaily(schema);
        new DaoGenerator().generateAll(schema, "./app/src/main/java-gen");
    }

    private static void addDaily(Schema schema) {
//        Entity node = schema.addEntity("Movie");
//        node.addIdProperty();
//        node.addStringProperty("text").notNull();
//        node.addStringProperty("comment");
//        node.addDateProperty("date");
        Entity daily = schema.addEntity("DailyORM");
        daily.addIdProperty();
        daily.addIntProperty("dailyid").primaryKey();
    }
}
