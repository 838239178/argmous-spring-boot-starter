package cn.shijh.argmous.spring.factory;

import cn.shijh.argmous.model.ArgumentInfo;
import org.aspectj.lang.JoinPoint;

import java.util.Collection;

public interface SpringArgumentInfoFactory {
    Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp);
}
