package com.meshchat.client.pipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @deprecated
 */
public class file {

    public static void main (String args[]) throws IOException {

        File f = new File("/tmp/command.sock");
//        FileReader fr = new FileReader(f);
//        while(f.canRead()) {
//            System.out.println(fr.read());
//        }
    }
}
