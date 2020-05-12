package com.example.firstpage.AttractionFragment;

import java.util.Comparator;

public class Attraction {
    String spotAddress;
    String spotname;
    String spotpic;
    String spotid;
    Double dis;
    String binhour;
//    String add;
    String tel;
    String country;

    String spotlong;
    String spotlat;
    double all37;
    double all55;
    double all73;
    double mon37;
    double mon55;
    double mon73;

//    public Attraction(String spotAddress,String spotname,String spotpic,String spotid,Double dis,String binhour
//            ,String tel, String spotlong, String spotlat,String country,String all37,String all55,String all73,String mon37,String mon55,String mon73)

    public Attraction(String spotAddress,String spotname,String spotpic,String spotid,Double dis,String binhour
            ,String tel, String spotlong, String spotlat,String country,String all37,String all55,String all73,String mon37,String mon55,String mon73) {

        setSpotid(spotid);
//        this.spotid = spotid;
        this.spotAddress = spotAddress;
        this.spotname = spotname;
        this.spotpic = spotpic;
        this.spotid=spotid;
        this.dis=dis;
        this.binhour=binhour;
//        this.add=add;
        this.tel=tel;
        this.spotlong = spotlong;
        this.spotlat = spotlat;
        this.country = country;
//        this.all37=Double.parseDouble(all37);
//        this.all55=Double.parseDouble(all55);
//        this.all73=Double.parseDouble(all73);
//        this.mon37=Double.parseDouble(mon37);
//        this.mon55=Double.parseDouble(mon55);
//        this.mon73=Double.parseDouble(mon73);

        if(all37==null){
            this.all37=0.0;
        }else{
            this.all37=Double.parseDouble(all37);
        }

        if(all55==null){
            this.all55=0.0;
        }else{
            this.all55=Double.parseDouble(all55);
        }

        if(all73==null){
            this.all73=0.0;
        }else{
            this.all73=Double.parseDouble(all73);
        }

        if(mon37==null){
            this.mon37=0.0;
        }else{
            this.mon37=Double.parseDouble(mon37);
        }

        if(mon55==null){
            this.mon55=0.0;
        }else{
            this.mon55=Double.parseDouble(mon55);
        }

        if(mon73==null){
            this.mon73=0.0;
        }else{
            this.mon73=Double.parseDouble(mon73);
        }

    }

    public String getspotAddress() {
        return spotAddress;
    }

    public void spotAddress(String spotAddress) {
        this.spotAddress = spotAddress;
    }

    public String getcountry() {
        return spotAddress;
    }

    public void setcountry(String country) {
        this.country = country;
    }

    public String getspotname() {
        return spotname;
    }

    public void setspotname(String spotname) {
        this.spotname = spotname;
    }


    public String getspotpic() {
        return spotpic;
    }

    public void setspotpic(String spotpic) {
        this.spotpic = spotpic;
    }

    public String getSpotid() {
        return spotid;
    }


    public void setSpotid(String spotid) {
        this.spotid = spotid;
    }

    //
    public String getSpotlong() {
        return spotlong;
    }
    //
    public void setSpotlong(String spotlong) {
        this.spotlong = spotlong;
    }

    public String getSpotlat() {
        return spotlat;
    }
//

    public void setSpotlat(String spotlat) {
        this.spotlat = spotlat;
    }

    public Double getDis() {
        return dis;
    }
    public void setDis(Double dis) {
        this.dis = dis;
    }

    public String getBinhour() {
        return binhour;
    }
    public void setBinhour(String binhour) {
        this.binhour = binhour;
    }

//    public String getAdd() {
//        return add;
//    }
//    public void setAdd(String add) {
//        this.add = add;
//    }

    public Double getall37() {
        return all37;
    }
    public void setall37(Double all37) {
        this.all37 = all37;
    }

    public Double getAll55() {
        return all55;
    }
    public void setall55(Double all55) {
        this.all55 = all55;
    }

    public Double getAll73() {
        return all73;
    }
    public void setall73(Double all73) {
        this.all73 = all73;
    }

    public Double getMon37() {
        return mon37;
    }
    public void setMon37(Double mon37) {
        this.mon37 = mon37;
    }

    public Double getMon55() {
        return mon55;
    }
    public void setMon55(Double mon55) {
        this.mon55 = mon55;
    }

    public Double getMon73() {
        return mon73;
    }
    public void setMon73(Double mon73) {
        this.mon73 = mon73;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String add) {
        this.tel = tel;
    }


    public static Comparator<Attraction> byhota37=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getall37().compareTo(two.getall37());
        }
    };
    public static Comparator<Attraction> byhota55=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getAll55().compareTo(two.getAll55());
        }
    };

    public static Comparator<Attraction> byhota73=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getAll73().compareTo(two.getAll73());
        }
    };

    public static Comparator<Attraction> byhotm37=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getMon37().compareTo(two.getMon37());
        }
    };

    public static Comparator<Attraction> byhotm55=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getMon55().compareTo(two.getMon55());
        }
    };

    public static Comparator<Attraction> byhotm73=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -one.getMon73().compareTo(two.getMon73());
        }
    };
    public static Comparator<Attraction> bydis=new Comparator<Attraction>() {
        @Override
        public int compare(Attraction one, Attraction two) {
            return -Double.valueOf(one.getDis()).compareTo(Double.valueOf(two.getDis()));
        }
    };
}
