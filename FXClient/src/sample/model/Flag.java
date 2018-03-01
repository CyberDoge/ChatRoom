package sample.model;

public enum Flag {
    MESSAGE("m/"), EXIT("ex/"), WARNING("w/"), ERROR("e/"), REGISTER("r/"), LOGIN("l/"), SUCCESS("s/"), UNEXPECTED("ux/");
   public  String name;
   Flag(String name){
       this.name = name;
   }
}
