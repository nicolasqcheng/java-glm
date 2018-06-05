package com.github.chen0040.glm.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by nicolasqcheng on 5/15/2018.
 */
public class CollectionUtils {
   public static <T> List<T> clone(List<T> that) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.size(); ++i){
         result.add(that.get(i));
      }
      return result;
   }

/*
   public static <T> List<T> toList(T[] that) {
      List<T> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(that[i]);
      }
      return result;
   }
*/
   public static List<Double> toList(double[] that) {
      List<Double> result = new ArrayList<>();
      for(int i=0; i < that.length; ++i){
         result.add(that[i]);
      }
      return result;
   }

   public static <T> void exchange(List<T> a, int i, int j) {
      T temp = a.get(i);
      a.set(i, a.get(j));
      a.set(j, temp);
   }


   public static double[] toDoubleArray(List<Double> list) {
      double[] result = new double[list.size()];
      for(int i=0; i < list.size(); ++i) {
         result[i] = list.get(i);
      }
      return result;
   }

   public static String[] toArray(List<String> list) {
      String[] result = new String[list.size()];
      for(int i=0; i < list.size(); ++i) {
         result[i] = list.get(i);
      }
      return result;
   }
}
