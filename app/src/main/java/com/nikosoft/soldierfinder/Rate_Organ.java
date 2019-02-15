package com.nikosoft.soldierfinder;

/**
 * Created by Yosef on 06/11/2017.
 */

public class Rate_Organ {

    public String[] Rate =
            {"سرباز سوم",
                    "سرباز دوم",
                    "سرباز یکم",
                    "سرجوخه",
                    "گروهبان سوم",
                    "گروهبان دوم",
                    "گروهبان یکم",
                    "استوار دوم",
                    "استوار یکم",
                    "ستوان سوم",
                    "ستوان دوم",
                    "ستوان یکم"};

    public String[] Organ =
            {"ارتش جمهوری اسلامی ایران",
                    "سپاه پاسداران انقلاب اسلامی",
                    "نیروی انتظامی جمهوری اسلامی ایران"
            };
    public String[] Artesh =
            {
                    "نیروی دریایی",
                    "نیروی زمینی",
                    "نیروی هوایی",
                    "پدافند هوایی"
            };
    public String[] Sepah =
            {
                    "نیروی زمینی",
                    "نیروی دریایی",
                    "نیروی هوایی"
            };
    public String[] Naja =
            {
                    "نیروی انتظامی",
                    "راهنمایی رانندگی",
                    "مرز بانی"
            };

    public String GetSubOrgan(int organ_pos, int sub_organ_pos) {
        sub_organ_pos-=1;
        switch (organ_pos) {
            case 1:
                return Artesh[sub_organ_pos];

            case 2:
                return Sepah[sub_organ_pos];

            case 3:
                return Naja[sub_organ_pos];
        }
        return "";
    }

    public String[] GetSubOrgan(int organ) {

        switch (organ) {
            case 1:
                return Artesh;
            case 2:
                return Sepah;
            case 3:
                return Naja;

                default:
                    return new String[]{""};
        }
    }
}
