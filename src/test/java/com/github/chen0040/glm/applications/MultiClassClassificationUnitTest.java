package com.github.chen0040.glm.applications;


import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.evaluators.ClassifierEvaluator;
import com.github.chen0040.glm.solvers.Glm;
import com.github.chen0040.glm.solvers.OneVsOneGlmClassifier;
import com.github.chen0040.glm.fileUtils.FileUtils;
import com.github.chen0040.data.utils.TupleTwo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Ignore;
import org.junit.Test;


/**
 * Created by xschen on 6/5/2017.
 */

public class MultiClassClassificationUnitTest {


   private static final Logger logger = LoggerFactory.getLogger(MultiClassClassificationUnitTest.class);

   @Ignore("not ready yet") @Test
   public void test_iris() throws IOException {
      InputStream irisStream = FileUtils.getResource("iris.data");
      DataFrame irisData = DataQuery.csv(",")
              .from(irisStream)
              .selectColumn(0).asNumeric().asInput("Sepal Length")
              .selectColumn(1).asNumeric().asInput("Sepal Width")
              .selectColumn(2).asNumeric().asInput("Petal Length")
              .selectColumn(3).asNumeric().asInput("Petal Width")
              .selectColumn(4).asCategory().asOutput("Iris Type")
              .build();

      TupleTwo<DataFrame, DataFrame> parts = irisData.shuffle().split(0.9);

      DataFrame trainingData = parts._1();
      DataFrame crossValidationData = parts._2();

      System.out.println(crossValidationData.head(10));

      OneVsOneGlmClassifier multiClassClassifier = Glm.oneVsOne(Glm::logistic);
      multiClassClassifier.fit(trainingData);

      ClassifierEvaluator evaluator = new ClassifierEvaluator();

      for(int i=0; i < crossValidationData.rowCount(); ++i) {
         String predicted = multiClassClassifier.classify(crossValidationData.row(i));
         String actual = crossValidationData.row(i).categoricalTarget();
         System.out.println("predicted: " + predicted + "\tactual: " + actual);
         evaluator.evaluate(actual, predicted);
      }

      evaluator.report();
   }

   @Ignore("not ready yet") @Test
   public void test_testdata() throws IOException {
      InputStream is = FileUtils.getResource("testdata.csv");
      DataFrame dataFrame = DataQuery.csv(",")
              .from(is)
              .selectColumn(0).asCategory().asInput("a")
              .selectColumn(1).asCategory().asInput("b")
              .selectColumn(2).asNumeric().asInput("c")
              .selectColumn(3).asNumeric().asInput("d")
              .selectColumn(4).asCategory().asInput("e")
              .selectColumn(5).asCategory().asInput("f")
              .selectColumn(6).asNumeric().asInput("g")
              .selectColumn(7).asNumeric().asInput("h")
              .selectColumn(8).asCategory().asInput("i")
              .selectColumn(9).asCategory().asInput("j")
              .selectColumn(10).asCategory().asOutput("OUT")
              .build();

      TupleTwo<DataFrame, DataFrame> parts = dataFrame.shuffle().split(0.9);

      DataFrame trainingData = parts._1();
      DataFrame crossValidationData = parts._2();

      System.out.println(crossValidationData.head(10));

      OneVsOneGlmClassifier multiClassClassifier = Glm.oneVsOne(Glm::logistic);

      multiClassClassifier.fit(trainingData);

      ClassifierEvaluator evaluator = new ClassifierEvaluator();

      for(int i=0; i < crossValidationData.rowCount(); ++i) {
         String predicted = multiClassClassifier.classify(crossValidationData.row(i));
         String actual = crossValidationData.row(i).categoricalTarget();
         System.out.println("predicted: " + predicted + "\tactual: " + actual);
         evaluator.evaluate(actual, predicted);
      }

      evaluator.report();



   }
   
}
