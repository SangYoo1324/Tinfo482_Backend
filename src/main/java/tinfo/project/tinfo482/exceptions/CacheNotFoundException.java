package tinfo.project.tinfo482.exceptions;

public class CacheNotFoundException extends Exception{
    public CacheNotFoundException(String message){
        super(message);
    }


    public void printStackTrace() {
        super.printStackTrace();
        System.out.println("Cache Not found");
    }
}
