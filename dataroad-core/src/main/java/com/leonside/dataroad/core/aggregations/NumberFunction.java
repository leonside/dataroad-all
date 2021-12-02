package com.leonside.dataroad.core.aggregations;

import org.apache.commons.lang.math.NumberUtils;

public interface NumberFunction{
        Object create();
        Object sum(Object o1, Object o2);
        long count(long o1);
        Object max(Object o1, Object o2);
        Object min(Object o1, Object o2);
        Double avg(Object o1, Object o2);
        Double avgTotal(Object o1, long size);

  public static NumberFunction getForClass(Class<?> clazz) {

        if (clazz == Integer.class) {
                return new IntegerFunction();
        } else if (clazz == Long.class) {
                return new LongFunction();
        } else if (clazz == Short.class) {
                return new ShortFunction();
        } else if (clazz == Double.class) {
                return new DoubleFunction();
        } else if (clazz == Float.class) {
                return new FloatFunction();
        }  else {
                throw new RuntimeException(
                        "DataStream cannot be summed because the class "
                                + clazz.getSimpleName()
                                + " does not support the + operator.");
        }
}


        public static class IntegerFunction implements NumberFunction {

        @Override
        public Object create() {
                return new Integer(0);
        }
        @Override
        public Object sum(Object o1, Object o2) {
                return (Integer)o1 + (Integer) o2;
        }
        @Override
        public long count(long o1) {
                return o1 + 1;
        }
        @Override
        public Object max(Object o1, Object o2) {
                return Math.max((Integer)o1,(Integer)o2);
        }
        @Override
        public Object min(Object o1, Object o2) {
                return  Math.min( (Integer)o1,(Integer)o2);
        }
        @Override
        public Double avg(Object o1, Object o2) {
                Double o1double = (o1 instanceof Integer) ? Double.valueOf((Integer)o1)  : (Double) o1;
                Double o2double = (o2 instanceof Integer) ? Double.valueOf((Integer)o2)  : (Double) o2;
                return (o1double + o2double) / 2;
        }
        @Override
        public Double avgTotal(Object o1, long size) {
                return Double.valueOf((Integer)o1)/ size;
        }
}
        public static class LongFunction implements NumberFunction {
        @Override
        public Object create() {
                return new Long(0);
        }
        @Override
        public Object sum(Object o1, Object o2) {
                return (Long)o1 + (Long) o2;
        }
        @Override
        public long count(long o1) {
                return o1 + 1;
        }
        @Override
        public Object max(Object o1, Object o2) {
                return Math.max((Long)o1,(Long)o2);
        }
        @Override
        public Object min(Object o1, Object o2) {
                return  Math.min((Long)o1,(Long)o2);
        }
        @Override
        public Double avg(Object o1, Object o2) {
                Double o1double = (o1 instanceof Long) ? Double.valueOf((Long)o1)  : (Long) o1;
                Double o2double = (o2 instanceof Long) ? Double.valueOf((Long)o2)  : (Long) o2;
                return (o1double + o2double) / 2;
        }
        @Override
        public Double avgTotal(Object o1, long size) {
                return Double.valueOf((Long)o1)/ size;
        }
}

        public static class ShortFunction implements NumberFunction {
        @Override
        public Object create() {
                return new Short((short) 0);
        }
        @Override
        public Object sum(Object o1, Object o2) {
                return (Short)o1 + (Short) o2;
        }
        @Override
        public long count(long o1) {
                return o1 + 1;
        }
        @Override
        public Object max(Object o1, Object o2) {
                return Math.max((Short)o1,(Short)o2);
        }
        @Override
        public Object min(Object o1, Object o2) {
                return  Math.min((Short)o1,(Short)o2);
        }
        @Override
        public Double avg(Object o1, Object o2) {
                Double o1double = (o1 instanceof Short) ? Double.valueOf((Short)o1)  : (Short) o1;
                Double o2double = (o2 instanceof Short) ? Double.valueOf((Short)o2)  : (Short) o2;
                return (o1double + o2double) / 2;
        }
        @Override
        public Double avgTotal(Object o1, long size) {
                return Double.valueOf((Short)o1)/ size;
        }
}

        public static class FloatFunction implements NumberFunction {
        @Override
        public Object create() {
                return new Float(0);
        }
        @Override
        public Object sum(Object o1, Object o2) {
                return (Float)o1 + (Float) o2;
        }
        @Override
        public long count(long o1) {
                return o1 + 1;
        }
        @Override
        public Object max(Object o1, Object o2) {
                return Math.max((Float)o1,(Float)o2);
        }
        @Override
        public Object min(Object o1, Object o2) {
                return  Math.min( (Float)o1,(Float)o2);
        }
        @Override
        public Double avg(Object o1, Object o2) {
                Double o1double = (o1 instanceof Float) ? Double.valueOf((Float)o1)  : (Float) o1;
                Double o2double = (o2 instanceof Float) ? Double.valueOf((Float)o2)  : (Float) o2;
                return (o1double + o2double) / 2;
        }
        @Override
        public Double avgTotal(Object o1, long size) {
                return Double.valueOf((Float)o1)/ size;
        }
}

        public static class DoubleFunction implements NumberFunction {
        @Override
        public Object create() {
                return new Double(0);
        }
        @Override
        public Object sum(Object o1, Object o2) {
                return (Double)o1 + (Double) o2;
        }
        @Override
        public long count(long o1) {
                return o1 + 1;
        }
        @Override
        public Object max(Object o1, Object o2) {
                return Math.max((Double)o1,(Double)o2);
        }
        @Override
        public Object min(Object o1, Object o2) {
                return  Math.min((Double)o1,(Double)o2);
        }
        @Override
        public Double avg(Object o1, Object o2) {
                return ((Double) o1 + (Double) o2) / 2;
        }
        @Override
        public Double avgTotal(Object o1, long size) {
                return Double.valueOf((Double)o1)/ size;
        }
}
}