package com.example.firstpage.AttractionFragment;

import java.util.ArrayList;
import java.util.Comparator;

public class myAttraction {
    String myspotname;
    String myspotpic;
    String myspotid;
    Double dis;
    String add;
    String tel;
    String user;
    String spotlong;
    String spotlat;
    String country;
    String all37;
    String all55;
    String all73;
    String mon37;
    String mon55;
    String mon73;

    String privacy; //1203
    String likeCount; //1206

    public myAttraction(String myspotname,String myspotpic,String myspotid, Double dis,String spotlong,String spotlat,String add,String tel,String country,String all37,String all55,String all73,String mon37,String mon55,String mon73,String privacy,String likeCount) {
        setMySpotid(myspotid);
        this.myspotname = myspotname;
        this.user = user;
        this.myspotpic = myspotpic;
        this.spotlong = spotlong;
        this.spotlat = spotlat;
        this.myspotid=myspotid;
        this.dis=dis;
        this.add=add;
        this.tel=tel;
        this.country=country;
        this.all37=all37;
        this.all55=all55;
        this.all73=all73;
        this.mon37=mon37;
        this.mon55=mon55;
        this.mon73=mon73;
        this.privacy = privacy;
        this.likeCount = likeCount;
    }

    public String getlikeCount() {
        return likeCount;
    }
    public void setlikeCount(String likeCount) {
        this.likeCount = likeCount;
    } //1206

    public String getAdd() {
        return add;
    }
    public void setAdd(String add) {
        this.add = add;
    }

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public String getPrivacy() {
        return privacy;
    }
    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

    public String getcountry() {
        return country;
    }
    public void setcountry(String country) {
        this.country = country;
    }

    public String getTel() {
        return tel;
    }
    public void setTel(String add) {
        this.tel = tel;

    }

    public String getmyspotname() {

        return myspotname;
    }

    public void setMySpotName(String myspotname) {
        this.myspotname = myspotname;
    }

    public Double getDis() {
        return dis;
    }
    public void setDis(Double dis) {
        this.dis = dis;
    }

    public String getmyspotpic() {

        return myspotpic;
    }

    public void setmyspotpic(String myspotpic) {
        this.myspotpic = myspotpic;
    }

    public String getMySpotid() {
        return myspotid;
    }

    public void setMySpotid(String myspotid) {
        this.myspotid = myspotid;
    }

    public String getSpotlong() {
        return spotlong;
    }
//
//    public void setSpotlong(String spotlong) {
//        this.spotlong = spotlong;
//    }
//
    public String getSpotlat() {
        return spotlat;
    }

    public String getall37() {
        return all37;
    }
    public void setall37(String all37) {
        this.all37 = all37;
    }

    public String getAll55() {
        return all55;
    }
    public void setall55(String all55) {
        this.all55 = all55;
    }

    public String getAll73() {
        return all73;
    }
    public void setall73(String all73) {
        this.all73 = all73;
    }

    public String getMon37() {
        return mon37;
    }
    public void setMon37(String mon37) {
        this.mon37 = mon37;
    }

    public String getMon55() {
        return mon55;
    }
    public void setMon55(String mon55) {
        this.mon55 = mon55;
    }

    public String getMon73() {
        return mon73;
    }
    public void setMon73(String mon73) {
        this.mon73 = mon73;
    }
//
//    public void setSpotlat(String spotlat) {
//        this.spotlat = spotlat;
//    }

    public static Comparator<myAttraction> byhota37= new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getall37().compareTo(two.getall37());
        }
    };
    public static Comparator<myAttraction> byhota55=new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getAll55().compareTo(two.getAll55());
        }
    };

    public static Comparator<myAttraction> byhota73=new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getAll73().compareTo(two.getAll73());
        }
    };

    public static Comparator<myAttraction> byhotm37=new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getMon37().compareTo(two.getMon37());
        }
    };

    public static Comparator<myAttraction> byhotm55=new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getMon55().compareTo(two.getMon55());
        }
    };

    public static Comparator<myAttraction> byhotm73=new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -one.getMon73().compareTo(two.getMon73());
        }
    };

    public static Comparator<myAttraction> bydis= new Comparator<myAttraction>() {
        @Override
        public int compare(myAttraction one, myAttraction two) {
            return -Double.valueOf(one.getDis()).compareTo(Double.valueOf(two.getDis()));        }
    };

}

