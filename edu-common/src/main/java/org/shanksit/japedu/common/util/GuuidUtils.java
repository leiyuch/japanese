package org.shanksit.japedu.common.util;

/**
 * @author admin
 * @version 1.0.0
 * @ClassName GuuidUtils.java
 * @Description TODO
 * @createTime 2020年09月30日 14:07:00
 */
public class GuuidUtils {


        private static long machineId = 0;

        private static long datacenterId = 0;
        /**
         * 单例模式创建学法算法对象
         * */
        private enum SnowFlakeSingleton{
            /**
             * 单例对象
             */
            Singleton;
            /**
             * 单例雪花对象
             */
            private SnowFlake snowFlake;
            /**
             * 单例对象
             */
            SnowFlakeSingleton(){
                snowFlake = new SnowFlake(datacenterId,machineId);
            }
            /**
             * 单例对象
             */
            public SnowFlake getInstance(){
                return snowFlake;
            }
        }


        public static long getUUID(){
            return SnowFlakeSingleton.Singleton.getInstance().nextId();
        }

//        public static void main(String[] args) {
//            CountDownLatch latch = new CountDownLatch(10000);
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < 10000; i++) {
//                new Runnable() {
//                    @Override
//                    public void run() {
//                        System.out.println(GuuidUtils.getUUID());
//                        latch.countDown();
//                    }
//                }.run();
//            }
//            try {
//                System.out.println("主线程"+Thread.currentThread().getName()+"等待子线程执行完成...");
//                latch.await();//阻塞当前线程，直到计数器的值为0
//                System.out.println("主线程"+Thread.currentThread().getName()+"开始执行...");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.print("雪花算法用时： ");
//            System.out.println(System.currentTimeMillis() - start);
//
//        }


}
