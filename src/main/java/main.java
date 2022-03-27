import es.upm.etsisi.cf4j.data.BenchmarkDataModels;
import es.upm.etsisi.cf4j.data.DataModel;

import es.upm.etsisi.cf4j.util.optimization.GridSearchCV;
import es.upm.etsisi.cf4j.util.optimization.ParamsGrid;
import gpmf.GPMF;
import qualityMeasures.prediction.MSE;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class main {
  public static void main(String[] args) throws IOException {

    DataModel datamodel = BenchmarkDataModels.MovieLens100K();

    Map<String, Object> params = new HashMap<String, Object>();

    params.put("numFactors", 6);
    params.put("regularization", 0.095);
    params.put("learningRate", 0.001);
    params.put("gens", 150);
    params.put("pbmut", 0.4);
    params.put("pbx", 1.0);
    params.put("popSize", 80);
    params.put("numIters", 100);
    params.put("maxDepthInit", 5);
    params.put("maxDepthFinal", 100);
    params.put("maxNodesInit", 20);
    params.put("maxNodesFinal", 300);
    params.put("numChildren", 80);
    params.put("earlyStoppingValue", 0.0001);
    params.put("earlyStoppingCount", 10);
    params.put("seed", 42L);

    GPMF gpmf = new GPMF(datamodel, params);
    gpmf.fit();

  }
}
