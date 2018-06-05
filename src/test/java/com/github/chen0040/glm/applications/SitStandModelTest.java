package com.github.chen0040.glm.applications;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.glm.enums.GlmSolverType;
import com.github.chen0040.data.evaluators.BinaryClassifierEvaluator;
import com.github.chen0040.glm.solvers.Glm;
import com.github.chen0040.glm.fileUtils.FileUtils;
import com.github.chen0040.data.utils.TupleTwo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;


import java.io.IOException;
import java.io.InputStream;

import java.util.Random;

/**
 * Created by qcheng on 3/31/2018.
 */
public class SitStandModelTest {

    private static final Logger logger = LoggerFactory.getLogger(SitStandModelTest.class);
 
    private static Random random = new Random();

    @Test
    public void test_sitstanddata() throws IOException {
        InputStream rawStream = FileUtils.getResource("sitStand_trainingSet.csv");
        DataFrame rawData = DataQuery.csv(",")
              .from(rawStream)
              .selectColumn(0).asNumeric().asInput("meanMagAcc")
              .selectColumn(1).asNumeric().asInput("stdMagAcc")
              .selectColumn(2).asNumeric().asInput("meanRoll")
              .selectColumn(3).asNumeric().asInput("stdRoll")
              .selectColumn(4).asNumeric().asInput("meanPitch")
              .selectColumn(5).asNumeric().asInput("stdPitch")
              .selectColumn(6).asNumeric().asInput("meanYaw")
              .selectColumn(7).asNumeric().asInput("stdYaw")
              //.selectColumn(8).asNumeric().asOutput("moving")
              .selectColumn(9).asNumeric().asOutput("label")
              //.selectColumn(10).asNumeric().asOutput("fileId")
              .build();
    TupleTwo<DataFrame, DataFrame> parts = rawData.shuffle().split(0.9);

    DataFrame trainingData = parts._1();
    DataFrame crossValidationData = parts._2();

    System.out.println(crossValidationData.head(10));
    Glm glm = Glm.linear();
      glm.setSolverType(GlmSolverType.GlmIrlsQr);
      glm.fit(trainingData);
      BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();
      for(int i = 0; i < crossValidationData.rowCount(); ++i){
        double predicted = glm.transform(crossValidationData.row(i));
        double actual = crossValidationData.row(i).target();
        logger.info("predicted: {}\texpected: {}", predicted, actual);
        //evaluator.evaluate(actual, predicted);
     }

     logger.info("Coefficients: {}", glm.getCoefficients());
     //evaluator.report();

    }
}
 