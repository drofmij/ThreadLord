package io.github.drofmij.threadlord.test;

import com.google.common.hash.Hashing;
import io.github.drofmij.threadlord.Minion;
import io.github.drofmij.threadlord.ThreadLord;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

public class TestThreadLord {

    /**
     * writes to multiple files, then reads multiple files and writes to 1 file
     * - implements Minion.work() inline for each section
     *
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testRunMulti() throws InterruptedException, ExecutionException {
        String tmpdirpath = "/tmp/threadlordtest/";
        File tmpdirfile = new File(tmpdirpath);
        tmpdirfile.mkdirs();
        int numpieces = 1000;
        int numthreads = 100;
        ThreadLord<String> threadLord = new ThreadLord<>(numthreads, false);
        for (int idx = 0; idx < numpieces; idx++) {
            Integer threadNum = idx;
            // write 1 file with 10000 lines of test data for each minion
            threadLord.addMinion(new Minion<String>(true) {
                @Override
                protected String work() {
                    try (FileWriter fw = new FileWriter(tmpdirpath + "tmp_" + threadNum);
                            BufferedWriter bw = new BufferedWriter(fw);) {
                        for (Integer line = 0; line < 10000; line++) {
                            String sha256hex = Hashing.sha256()
                                    .hashString(Long.toString(System.currentTimeMillis()) + "_" + line.toString(), StandardCharsets.UTF_8)
                                    .toString();
                            bw.write(sha256hex + "\n");
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(TestThreadLord.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return Long.toString(System.currentTimeMillis()) + "_" + threadNum.toString();
                }
            });
        }
        // run the Minions
        threadLord.run();
        try (FileWriter fw = new FileWriter(tmpdirpath + "tmp_combo");
                BufferedWriter bw = new BufferedWriter(fw);) {
            for (File file : tmpdirfile.listFiles()) {
                if (!file.getName().contains("combo")) {
                    // read each of the test files and write out to 1 result file
                    threadLord.addMinion(new Minion<String>(true) {
                        @Override
                        protected String work() {
                            try (FileReader fr = new FileReader(file);
                                    BufferedReader br = new BufferedReader(fr);) {
                                String line;
                                while ((line = br.readLine()) != null) {
                                    bw.write(line + "\n");
                                }
                            } catch (IOException ex) {
                                Logger.getLogger(TestThreadLord.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            file.delete();
                            return "";
                        }
                    });
                }
            }
            threadLord.run();
        } catch (IOException ex) {
            Logger.getLogger(TestThreadLord.class.getName()).log(Level.SEVERE, null, ex);
        }

        // shutdown the thread executor
        threadLord.shutdown();

        // cleanup temp files
        new File(tmpdirpath + "tmp_combo").delete();
        tmpdirfile.delete();
    }
}
