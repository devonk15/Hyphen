package dev.quantumfusion.hyphen.info;

import dev.quantumfusion.hyphen.ScanHandler;
import dev.quantumfusion.hyphen.gen.metadata.ArraySerializerMetadata;
import dev.quantumfusion.hyphen.gen.metadata.SerializerMetadata;
import dev.quantumfusion.hyphen.thr.ThrowHandler;
import dev.quantumfusion.hyphen.thr.exception.ClassScanException;
import dev.quantumfusion.hyphen.util.Color;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

import static dev.quantumfusion.hyphen.thr.ThrowEntry.of;

public class ArrayInfo extends TypeInfo {
	private final TypeInfo values;

	public ArrayInfo(Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, TypeInfo values) {
		super(clazz, annotations);
		this.values = values;
	}

	public static ArrayInfo create(TypeInfo source, Class<?> clazz, Map<Class<Annotation>, Annotation> annotations, TypeInfo values) {
		if (values == ScanHandler.UNKNOWN_INFO)
			throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
					of("Source Class", source.clazz.getName()),
					of("Error Class", clazz.getName()));
		return new ArrayInfo(clazz, annotations, values);
	}

	public static ArrayInfo createGeneric(ScanHandler handler, TypeInfo source, Map<Class<Annotation>, Annotation> annotations, Class<?> clazz, GenericArrayType genericArrayType, AnnotatedType annotatedType) {
		AnnotatedType annotatedArrayType;
		if (annotatedType instanceof AnnotatedArrayType type)
			annotatedArrayType = type.getAnnotatedGenericComponentType();
		else annotatedArrayType = null;

		TypeInfo values = handler.create(source, clazz, genericArrayType.getGenericComponentType(), annotatedArrayType);
		if (values == ScanHandler.UNKNOWN_INFO)
			throw ThrowHandler.fatal(ClassScanException::new, "Type could not be identified",
					of("Source Class", source.clazz.getName()),
					of("Error Class", clazz.getName()));

		return new ArrayInfo(clazz, annotations, values);
	}


	@Override
	public SerializerMetadata createMetadata(ScanHandler factory) {
		return new ArraySerializerMetadata(this, factory.createSerializeMetadata(this.values));
	}

	@Override
	public String toFancyString() {
		return this.values.toFancyString() + Color.YELLOW + "[]";
	}

	@Override
	public String getMethodName(boolean absolute) {
		return values.getMethodName(absolute) + "[]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof ArrayInfo arrayInfo)) return false;
		if (!super.equals(o)) return false;
		return Objects.equals(values, arrayInfo.values);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), values);
	}

	@Override
	public String toString() {
		return values.toString() + "[]";
	}

}
