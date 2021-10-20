package top.pressed.argmous.spring.factory;

import org.aspectj.lang.JoinPoint;
import top.pressed.argmous.model.ArgumentInfo;

import java.util.Collection;

public interface SpringArgumentInfoFactory {
    Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp, Collection<ArgumentInfo> fromMethod, Collection<ArgumentInfo> fromArray);

    Collection<ArgumentInfo> createFromJoinPint(JoinPoint jp);
}
