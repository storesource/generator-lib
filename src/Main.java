import com.storesource.generator.sequence.LongSequenceGenerator;

public class Main {

    public static void main(String[] args) {
        System.out.println("First   Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Second  Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Third   Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Fourth  Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Fifth   Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Sixth   Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Seventh Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Eighth  Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Ninth   Id: " + LongSequenceGenerator.INSTANCE.nextId());
        System.out.println("Tenth   Id: " + LongSequenceGenerator.INSTANCE.nextId());
    }
}
