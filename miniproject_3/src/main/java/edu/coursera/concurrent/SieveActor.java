package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {

    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor sieveActor = new SieveActorActor(2);

        PCDP.finish(() -> {
            for (int i = 3; i <= limit; i += 2) {
                sieveActor.send(i);
            }
            sieveActor.send(0);
        });

        int numberOfPrimes = 0;
        SieveActorActor actor = sieveActor;
        while (actor != null) {
            numberOfPrimes++;
            actor = actor.nextActor;
        }

        return numberOfPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {

        private int prime;

        private SieveActorActor nextActor;

        public SieveActorActor(final int prime) {
            this.prime = prime;
        }

        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;
            if (candidate > 0 && isLocalPrime(candidate))
                sendToNextActor(msg);
        }

        private void sendToNextActor(final Object msg) {
            if (nextActor == null)
                nextActor = new SieveActorActor((Integer) msg);
            else nextActor.send(msg);
        }

        private boolean isLocalPrime(final Integer candidate) {
            return candidate % prime != 0;
        }
    }
}
