package cn.shijh.argmous.spring.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

public class NoCacheManager implements CacheManager {
    static class NoCache implements Cache {
        @Override
        public String getName() {
            return null;
        }

        @Override
        public Object getNativeCache() {
            return null;
        }

        @Override
        public ValueWrapper get(Object o) {
            return null;
        }

        @Override
        public <T> T get(Object o, Class<T> aClass) {
            return null;
        }

        @Override
        public <T> T get(Object o, Callable<T> callable) {
            return null;
        }

        @Override
        public void put(Object o, Object o1) {

        }

        @Override
        public void evict(Object o) {

        }

        @Override
        public void clear() {

        }
    }

    @Override
    public Cache getCache(String s) {
        return new NoCache();
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.singletonList("");
    }
}
