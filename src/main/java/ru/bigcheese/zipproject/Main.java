package ru.bigcheese.zipproject;

public class Main {

    public static void main(String[] args) {
	    if (args.length != 1) {
            System.out.println("Error! Need a root directory parameter. Run application again with parameter.");
            System.exit(1);
        }
        Zipper zipper = new Zipper(args[0], new FilesFilter());
        zipper.zip(args[0] + ".zip");
    }
}
