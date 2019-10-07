package com.nix.plus.model.ienum;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author by keray
 * date:2019/7/26 16:01
 * 性别
 */
public enum Sex {
    //
    boy("男",0),
    girl("女",1),
    other("其他",2),
    unknown("未知",3);

    String desc;

    @EnumValue
    Integer code;

    Sex(String desc, Integer code) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCode() {
        return code;
    }


    public static Sex getSexType(String typeName) {
        Sex[] sexes = Sex.values();
        Sex sexType = null;
        for (Sex sex : sexes) {
            if (sex.name().equals(typeName)) {
                sexType = sex;
                break;
            }
        }
        return sexType;
    }

}
