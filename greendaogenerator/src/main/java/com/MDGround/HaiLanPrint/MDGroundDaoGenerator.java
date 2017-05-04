package com.MDGround.HaiLanPrint;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MDGroundDaoGenerator {

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "com.MDGround.HaiLanPrint.greendao");

        addLocationEntity(schema);

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }

    private static void addLocationEntity(Schema schema) {
        Entity location = schema.addEntity("Location");

        location.addLongProperty("LocationID").primaryKey();
        location.addStringProperty("EnglishName");
        location.addBooleanProperty("IsCity");
        location.addBooleanProperty("IsHot");
        location.addBooleanProperty("IsValid");
        location.addLongProperty("LocationCode");
        location.addStringProperty("LocationName");
        location.addFloatProperty("LocationX");
        location.addFloatProperty("LocationY");
        location.addLongProperty("ParentID");
    }
}
