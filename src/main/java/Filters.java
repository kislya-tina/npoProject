public class Filters {
    private String line;
    private String target;

    public Filters(String line, String target){
        this.line = line;
        this.target = target;
    }

    public Filters(String line){
        this.line = line;
    }

    public boolean isContainsTarget(){
        if(line.contains(target)){
            return true;
        }
        return false;
    }

    public static void piska(){
        String s = String.join("03.07.2016"
                , "Это просто длинная многострочная строка,"
                , "Это просто длинная многострочная строка,"
                , "Это просто длинная многострочная строка,"
                , "Это просто длинная многострочная строка,"
        );
        System.out.println(s);
    }
}