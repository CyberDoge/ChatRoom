package helper;

public enum Flag {
    MESSAGE("m/"), EXIT("ex/"), WARNING("w/"), ERROR("e/"), REGISTER("r/"), LOGIN("l/"), SUCCESS("s/"), UNEXPECTED("ux/");
    public String name;

    public  static Flag getFlag(String flag){
        if(flag.equals("m/")){
            return MESSAGE;
        } else if (flag.equals("ex/")){
            return EXIT;
        } else if (flag.equals("e/")){
            return ERROR;
        } else if (flag.equals("r/")){
            return REGISTER;
        }else if (flag.equals("l/")){
            return LOGIN;
        }else if (flag.equals("s/")){
            return SUCCESS;
        }else
        return UNEXPECTED;
    }
    Flag(String name) {
        this.name = name;
    }
}
