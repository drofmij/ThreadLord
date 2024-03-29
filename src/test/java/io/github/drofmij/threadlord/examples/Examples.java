package io.github.drofmij.threadlord.examples;

import io.github.drofmij.threadlord.Minion;
import io.github.drofmij.threadlord.ThreadLord;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.github.drofmij.threadlord.test.TestThreadLord;

public class Examples {

    /**
     * simple example - handle list of input strings in parallel threads and
     * gather the results into a list
     */
    public static void example1() {
        try {
            String[] stringsToProcess = {};
            // read input, build list of strings or other units of work in code, etc.
            
            // initialize ThreadLord with 10 threads, set runOnce to true, set status output true
            ThreadLord<String> threadLord = new ThreadLord<>(10, true, true);
            for (String toprocess : stringsToProcess) {
                /* 
                * inline implementation of Minion.work()
                * - allows access to containing class variables etc.
                * - reduces amount of Minion code to write (no constructor, or member vars needed)
                 */
                threadLord.add(new Minion<String>() {
                    @Override
                    protected String work() {
                        // perform operation here.
                        String result = toprocess;
                        return result;
                    }
                });
            }
            // run the thread executor until there are no minions left and get the results
            List<String> results = threadLord.run();
            
            // handle results
            
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(TestThreadLord.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
