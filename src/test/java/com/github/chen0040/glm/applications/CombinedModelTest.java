package com.github.chen0040.glm.applications;

import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.glm.enums.GlmDistributionFamily;
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
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Random;

/**
 * Created by qcheng on 4/1/2018.
 */
public class CombinedModelTest {
    private static final Logger logger = LoggerFactory.getLogger(CombinedModelTest.class);
 
    private static Random random = new Random();

    @Test
    public void test_combined_model ()throws IOException {
        //Train sit-stand model
        InputStream rawStreamSitStandTraining = FileUtils.getResource("sitStand_trainingSet_2.csv");
        DataFrame rawDataSitStandTraining = DataQuery.csv(",")
              .from(rawStreamSitStandTraining)
              .selectColumn(0).asNumeric().asInput("meanMagAcc")
              .selectColumn(1).asNumeric().asInput("stdMagAcc")
              .selectColumn(2).asNumeric().asInput("meanRoll")
              .selectColumn(3).asNumeric().asInput("stdRoll")
              .selectColumn(4).asNumeric().asInput("meanPitch")
              .selectColumn(5).asNumeric().asInput("stdPitch")
              .selectColumn(6).asNumeric().asInput("meanYaw")
              .selectColumn(7).asNumeric().asInput("stdYaw")
              .selectColumn(9).asNumeric().asOutput("label")
              .build();
        Glm glm_SitStand = Glm.linear();
        glm_SitStand.setDistributionFamily(GlmDistributionFamily.Normal);
        glm_SitStand.setSolverType(GlmSolverType.GlmIrls);
        glm_SitStand.fit(rawDataSitStandTraining);
        //Train sit good-bad model
        InputStream rawStreamGoodbadTraining_sit = FileUtils.getResource("sit_good_bad_training_set.csv");
        DataFrame rawDataGoodBadTraining_sit = DataQuery.csv(",")
              .from(rawStreamGoodbadTraining_sit)
              .selectColumn(0).asNumeric().asInput("meanMagAcc")
              .selectColumn(1).asNumeric().asInput("stdMagAcc")
              .selectColumn(2).asNumeric().asInput("meanRoll")
              .selectColumn(3).asNumeric().asInput("stdRoll")
              .selectColumn(4).asNumeric().asInput("meanPitch")
              .selectColumn(5).asNumeric().asInput("stdPitch")
              .selectColumn(6).asNumeric().asInput("meanYaw")
              .selectColumn(7).asNumeric().asInput("stdYaw")
              .selectColumn(9).asNumeric().asOutput("label")
              .build();
        Glm glm_sit_goodbad = Glm.linear();
        glm_sit_goodbad.setDistributionFamily(GlmDistributionFamily.Normal);
        glm_sit_goodbad.setSolverType(GlmSolverType.GlmIrls);
        glm_sit_goodbad.fit(rawDataGoodBadTraining_sit);
        //Train stand good-bad model
        InputStream rawStreamGoodbadTraining_stand = FileUtils.getResource("stand_good_bad_training_set.csv");
        DataFrame rawDataGoodBadTraining_stand = DataQuery.csv(",")
              .from(rawStreamGoodbadTraining_stand)
              .selectColumn(0).asNumeric().asInput("meanMagAcc")
              .selectColumn(1).asNumeric().asInput("stdMagAcc")
              .selectColumn(2).asNumeric().asInput("meanRoll")
              .selectColumn(3).asNumeric().asInput("stdRoll")
              .selectColumn(4).asNumeric().asInput("meanPitch")
              .selectColumn(5).asNumeric().asInput("stdPitch")
              .selectColumn(6).asNumeric().asInput("meanYaw")
              .selectColumn(7).asNumeric().asInput("stdYaw")
              .selectColumn(9).asNumeric().asOutput("label")
              .build();
        Glm glm_stand_goodbad = Glm.linear();
        glm_stand_goodbad.setDistributionFamily(GlmDistributionFamily.Normal);
        glm_stand_goodbad.setSolverType(GlmSolverType.GlmIrls);
        glm_stand_goodbad.fit(rawDataGoodBadTraining_stand);
        //Test on real data
        InputStream testStream = FileUtils.getResource("test_set_408_MBF_Sit_MG_SG_MB_SB_03-000.csv");
        DataFrame testData = DataQuery.csv(",")
            .from(testStream)
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
        BinaryClassifierEvaluator evaluator = new BinaryClassifierEvaluator();
        String fn = "./output_combine_test_test_set_408_MBF_Sit_MG_SG_MB_SB_03-000_full_data_0.6.txt";
        FileWriter fw = new FileWriter(fn);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i = 0; i < testData.rowCount(); ++i){
            double sitstand_predicted = glm_SitStand.transform(testData.row(i));
            String posture = sitstand_predicted<0.6?"sit":"stand";
            String status = "";
            double goodbad_predicted = 0;
            if (sitstand_predicted >=0.6 ) {
                goodbad_predicted=glm_stand_goodbad.transform(testData.row(i));
            }
            else {
                goodbad_predicted=glm_sit_goodbad.transform(testData.row(i));
            }
            status = goodbad_predicted<0.5?"Good":"Bad";
            logger.info("sitstand_predicted: {}\tposture: {}\tgoodbad_predicted: {}\tstatus: {}", sitstand_predicted, posture, goodbad_predicted, status);
            bw.write(sitstand_predicted + "," + posture + "," + goodbad_predicted + "," + status);
            bw.newLine();
            //evaluator.evaluate(actual, predicted);
        }
        bw.close();
        fw.close();

    }
}

