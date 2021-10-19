package dev.quantumfusion.hyphen.scan;

import dev.quantumfusion.hyphen.scan.type.*;
import dev.quantumfusion.hyphen.thr.HyphenException;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;

public class Clazzifier {
	public static Clazz create(@NotNull Type type, @Nullable Clazz ctx, Direction dir) {
		return create(ScanUtil.wrap(type), ctx, dir);
	}

	public static Clazz create(@NotNull AnnotatedType annotatedType, @Nullable Clazz ctx, Direction dir) {
		try {
			var type = annotatedType.getType();
			if (type instanceof ParameterizedType) return ParaClazz.create(annotatedType, ctx, dir);
			if (type instanceof GenericArrayType) return ArrayClazz.create(annotatedType, ctx, dir);
			if (type instanceof TypeVariable) return TypeClazz.create(annotatedType, ctx);
			if (type instanceof WildcardType) return UnknownClazz.UNKNOWN;
			if (type instanceof Class<?> c && c.getTypeParameters().length > 0)
				return ParaClazz.create(annotatedType, ctx, dir);
			if (type instanceof Class<?> c && c.isArray())
				return ArrayClazz.create(annotatedType, ctx, dir);
			if (type instanceof Class<?>)
				return Clazz.create(annotatedType, ctx);
			throw new RuntimeException("Can not handle: " + annotatedType.getClass().getSimpleName());
		} catch (Throwable throwable) {
			throw HyphenException.thr("class", ":", annotatedType, throwable);
		}
	}

}
