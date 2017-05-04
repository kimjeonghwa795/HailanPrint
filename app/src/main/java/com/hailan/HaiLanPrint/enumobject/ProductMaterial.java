package com.hailan.HaiLanPrint.enumobject;

/**
 * Created by yoghourt on 5/16/16.
 */
public enum ProductMaterial {

    PrintPhoto_Glossy("光面"),
    PrintPhoto_Matte("绒面"),
    ArtAlbum_Boutique("精品艺术册"),
    ArtAlbum_Crystal("水晶艺术册"),
    PhoneShell_Silicone("硅胶"),
    PhoneShell_Plastic("塑料"),
    Engraving_Crystal("水晶面"),
    Engraving_Yogon("亚光面");

    private String text;

    private ProductMaterial(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static ProductMaterial fromValue(String value) {
        for (ProductMaterial type : ProductMaterial.values()) {
            if (type.toString().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
