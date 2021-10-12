package dev.quantumfusion.hyphen.scan.type;

import dev.quantumfusion.hyphen.scan.Clazzifier;
import dev.quantumfusion.hyphen.util.ScanUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;

/**
 * Comes from a ParameterizedClazz. This holds the bounds and the actual.
 */
public class TypeClazz implements Clz {
	public final String name;
	private final FieldType actual;
	private Clz @Nullable [] bounds;
	private final Type[] rawBounds;
	private Clazz context;

	protected TypeClazz(String name, FieldType actual, Type[] rawBounds, Clazz context) {
		this.name = name;
		this.actual = actual;
		this.rawBounds = rawBounds;
		this.context = context;
	}


	public static TypeClazz createRaw(TypeVariable<?> typeVariable) {
		return new TypeClazz(typeVariable.getName(), Clazzifier.UNDEFINED_FIELD, typeVariable.getBounds(), null);
	}

	public void setContext(ParameterizedClazz context) {
		this.context = context;
	}

	public TypeClazz resolveFUCKActual(Clazz source) {
		// var defined = this.actual.resolve(source);
		// if (defined != this.actual) {
		// a change
		return new TypeClazz(this.name, this.actual.resolve(source), this.rawBounds, this.context);
	}

	@Override
	public TypeClazz instantiate(AnnotatedType annotatedType) {
		return this;
	}
	public TypeClazz apply(AnnotatedType annotatedType) {
		Clz clazz = this.actual.clazz();
		if (clazz == Clazzifier.UNDEFINED)
			// If i just gave the context with `instantiate` I could just put this in UNDEFINED
			return new TypeClazz(this.name, Clazzifier.createAnnotatedType(annotatedType, this.context), this.rawBounds, null);
		Clz instantiated = clazz.instantiate(annotatedType);

		if (clazz == instantiated) return this;

		return new TypeClazz(this.name, new FieldType(
				instantiated, this.actual.annotations(), this.actual.globalAnnotations()
		), this.rawBounds, null);
	}

	@Override
	public TypeClazz map(Clz other, Map<TypeClazz, TypeClazz> types, MergeDirection mergeDirection) {
		final TypeClazz orDefault = types.getOrDefault(this, this);
		if (!orDefault.equals(this))
			return orDefault.map(other, types, mergeDirection);

		if (other instanceof TypeClazz otherClazz) {
			TypeClazz otherFix = types.getOrDefault(otherClazz, otherClazz);

			if (otherFix.equals(otherClazz)) {
				// todo: this feels like it's missing shit
				return new TypeClazz(this.name, this.actual.map(otherFix.actual, types, mergeDirection), this.rawBounds, null);

			} else {
				TypeClazz merge = this.map(otherFix, types, mergeDirection);
				types.put(otherClazz, merge);
				return merge;
			}
		} else {
			TypeClazz typeClazz = new TypeClazz(this.name, this.actual.map(FieldType.of(other), types, mergeDirection), this.rawBounds, null);
			types.put(this, typeClazz);
			return typeClazz;
		}
	}

	@Override
	@SuppressWarnings({"AssignmentToForLoopParameter", "RedundantSuppression"})

	public TypeClazz resolve(Clazz context) {
		if (context.equals(this.context)) return this;
		var defined = context.resolveType(this.name);

		if (defined != null)
			return defined;

		return this;
	}

	@Override
	public void finish(AnnotatedType type, Clazz source) {
		this.context = source;
	}

	@Override
	public String toString() {
		return (this.context == null ? "" : this.context.clazz.getSimpleName() + ":") + this.name + " = " + this.actual.toString();
	}

	@Override
	public boolean equals(Object o) {
		return this == o
				|| o instanceof TypeClazz that
				&& this.name.equals(that.name)
				&& this.actual.equals(that.actual)
				&& Arrays.equals(this.bounds, that.bounds)
				&& (this.context == that.context || this.context == null || that.context == null);
	}

	@Override
	public int hashCode() {
		int result = this.name.hashCode();
		result = 31 * result + this.actual.hashCode();
		result = 31 * result + Arrays.hashCode(this.bounds);
		result = 31 * result + (this.context == null ? 0 : this.context.clazz.hashCode());
		return result;
	}

	@Override
	public Class<?> pullBytecodeClass() {
		return ScanUtil.getClassFrom(this.rawBounds[0]);
	}

	@Override
	public Class<?> pullClass() {
		return this.actual.pullClass();
	}
}
