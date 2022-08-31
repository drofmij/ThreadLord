
# ThreadLord
 **java utils for multi-threaded io operations**
 
ThreadLord handles simple thread management for a set of Minion objects and bundles the results into a list - very useful for heavy IO operations such as reading or writing from hundreds or thousands of files locally or in cloud data store such as S3 thread pool can be single use or can be reused multiple times for different workloads in the same application.

### Releases are in maven central: https://search.maven.org/artifact/io.github.drofmij/threadlord/0.2.0.3/jar

    <dependency>
      <groupId>io.github.drofmij</groupId>
      <artifactId>threadlord</artifactId>
      <version>0.2.0.3</version>
    </dependency>


## Example

     /**
     * simple example - handle list of input strings in parallel threads and
     * gather the results into a list
     */
    public static void example1() {
        try {
            String[] stringsToProcess = {};
            // read input, build list of strings in code, etc.
            
            // initialize ThreadLord with 10 threads, set runOnce to true, set status output true
            ThreadLord<String> threadLord = new ThreadLord<>(10, true, true);
            for (String toprocess : stringsToProcess) {
                /* 
                * inline implementation of Minion.work()
                * - allows access read-only to containing class variables etc.
                * - reduces amount of Minion code to write
                 */
                threadLord.addMinion(new Minion<String>() {
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
        }
    }
