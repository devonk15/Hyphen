package dev.quantumfusion.hyphen.codegen.def;

import dev.quantumfusion.hyphen.Options;
import dev.quantumfusion.hyphen.codegen.CodegenHandler;
import dev.quantumfusion.hyphen.codegen.MethodHandler;
import dev.quantumfusion.hyphen.codegen.MethodInfo;
import dev.quantumfusion.hyphen.scan.type.Clazz;

import java.util.Map;

import static org.objectweb.asm.Opcodes.ILOAD;

public abstract class MethodDef implements SerializerDef {
	public final MethodInfo getInfo;
	public final MethodInfo putInfo;
	public final MethodInfo measureInfo;
	public final Clazz clazz;
	public final Map<Options, Boolean> options;

	public MethodDef(CodegenHandler<?, ?> handler, Clazz clazz){
		this(handler, clazz, clazz.toString());
	}

	public MethodDef(CodegenHandler<?, ?> handler, Clazz clazz, String name) {
		this.clazz = clazz;
		this.options = handler.options;
		final Class<?> definedClass = clazz.getDefinedClass();
		this.getInfo = handler.apply(new MethodInfo("get" + name, definedClass, handler.ioClass));
		this.putInfo = handler.apply(new MethodInfo("put" + name, Void.TYPE, handler.ioClass, definedClass));
		this.measureInfo = handler.apply(new MethodInfo("measure" + name, int.class, definedClass));
	}

	public abstract void writeMethodPut(MethodHandler mh, Runnable valueLoad);

	public abstract void writeMethodGet(MethodHandler mh);

	public abstract void writeMethodMeasure(MethodHandler mh, Runnable valueLoad);

	@Override
	public void writePut(MethodHandler mh, Runnable valueLoad) {
		mh.varOp(ILOAD, "io");
		valueLoad.run();
		mh.callInst(putInfo);
	}

	@Override
	public void writeGet(MethodHandler mh) {
		mh.varOp(ILOAD, "io");
		mh.callInst(getInfo);
	}

	@Override
	public void writeMeasure(MethodHandler mh, Runnable valueLoad) {
		valueLoad.run();
		mh.callInst(measureInfo);
	}
}
