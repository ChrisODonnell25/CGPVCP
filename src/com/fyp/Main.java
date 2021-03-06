package com.fyp;

import com.fyp.VCPAlgorithms.Edge;
import com.fyp.VCPAlgorithms.VCPInstance;
import com.fyp.cgp.functions.*;
import com.fyp.cgp.genes.Individual;
import com.fyp.cgp.genes.VCPGeneration;
import com.fyp.cgp.genes.VCPIndividual;
import com.fyp.cgp.nodes.ConnectionNode;
import com.fyp.cgp.nodes.GenericNode;
//import org.mariuszgromada.math.mxparser.Expression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args){
		/*        double[] input = {0,0.1,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1,1.1,1.2,1.3,1.4,1.5,1.6,1.7,1.8,1.9,2,2.1,2.2,2.3,2.4,2.5,2.6,2.7,2.8,2.9,3,3.1,3.2,3.3,3.4,3.5,3.6,3.7,3.8,3.9,4,4.1,4.2,4.3,4.4,4.5,4.6,4.7,4.8,4.9,5,5.1,5.2,5.3,5.4,5.5,5.6,5.7,5.8,5.9,6,6.1,6.2,6.3,6.4,6.5,6.6,6.7,6.8,6.9,7,7.1,7.2,7.3,7.4,7.5,7.6,7.7,7.8,7.9,8,8.1,8.2,8.3,8.4,8.5,8.6,8.7,8.8,8.9,9,9.1,9.2,9.3,9.4,9.5,9.6,9.7,9.8,9.9,10};
		//        double[] output = {0,0.099833417,0.198669331,0.295520207,0.389418342,0.479425539,0.564642473,0.644217687,0.717356091,0.78332691,0.841470985,0.89120736,0.932039086,0.963558185,0.98544973,0.997494987,0.999573603,0.99166481,0.973847631,0.946300088,0.909297427,0.863209367,0.808496404,0.745705212,0.675463181,0.598472144,0.515501372,0.42737988,0.33498815,0.239249329,0.141120008,0.041580662,-0.058374143,-0.157745694,-0.255541102,-0.350783228,-0.442520443,-0.529836141,-0.611857891,-0.687766159,-0.756802495,-0.818277111,-0.871575772,-0.916165937,-0.951602074,-0.977530118,-0.993691004,-0.999923258,-0.996164609,-0.982452613,-0.958924275,-0.925814682,-0.883454656,-0.832267442,-0.772764488,-0.705540326,-0.631266638,-0.550685543,-0.464602179,-0.373876665,-0.279415498,-0.182162504,-0.083089403,0.0168139,0.116549205,0.215119988,0.311541364,0.404849921,0.494113351,0.578439764,0.656986599,0.72896904,0.793667864,0.850436621,0.898708096,0.937999977,0.967919672,0.988168234,0.998543345,0.998941342,0.989358247,0.969889811,0.940730557,0.902171834,0.854598908,0.798487113,0.734397098,0.66296923,0.584917193,0.501020856,0.412118485,0.319098362,0.222889914,0.124454424,0.024775425,-0.07515112,-0.174326781,-0.271760626,-0.366479129,-0.457535894,-0.544021111};
		double[] input = {-2.878041, -2.8207904, -2.7592952, -2.6963685, -2.6385082, -2.5797204, -2.5180719, -2.4656775, -2.4017367, -2.3390774, -2.2795385, -2.2186483, -2.1623016, -2.0998788, -2.0389799, -1.9810136, -1.9193948, -1.8607397, -1.7970327, -1.742626, -1.6748463, -1.6193431, -1.5583775, -1.4979153, -1.4409212, -1.3786279, -1.3172623, -1.2553638, -1.197551, -1.1416502, -1.0801214, -1.0259154, -0.9602664, -0.90526058, -0.8402093, -0.7780571, -0.71756538, -0.65963338, -0.60034726, -0.53871947, -0.47982797, -0.41809618, -0.36424803, -0.29909566, -0.24295717, -0.17758144, -0.11720929, -0.059214931, 0.000014084, 0.059431729, 0.12439735, 0.17993548, 0.23893258, 0.29984805, 0.3636934, 0.42105506, 0.48385637, 0.53935877, 0.6026218, 0.65972226, 0.72247837, 0.77860414, 0.83728378, 0.90126406, 0.95971039, 1.0161987, 1.0816396, 1.1414502, 1.2027106, 1.2612802, 1.3207585, 1.3865517, 1.4430817, 1.5027562, 1.5601011, 1.6250318, 1.6812095, 1.7426657, 1.7978797, 1.8585339, 1.9208284, 1.9752259, 2.0398898, 2.0977374, 2.1605158, 2.2183853, 2.2803063, 2.3377299, 2.3988236, 2.4559455, 2.5159598, 2.5805067, 2.6365348, 2.7003717, 2.7645455, 2.8197829, 2.8770764, 2.9403095};
		double[] output = {-2.2394861, -2.0397598, -2.6309724, -2.8518024, -2.166931, -3.2425239, -3.8557676, -3.3860766, -3.294823, -3.3140675, -2.9171638, -2.6663871, -2.9524565, -2.5182995, -2.1563921, -1.5863988, -1.9073096, -1.6726193, -1.6467396, -1.1911957, -0.30394286, -0.74253257, -0.23921057, 0.49094885, 0.92558001, 1.2876288, 0.95805119, 1.6211995, 1.9684452, 0.99838244, 1.5496329, 2.2133872, 1.923719, 1.7386141, 2.1316752, 1.8518066, 1.2159271, 1.4840545, 0.8961618, 1.1239869, 1.2861245, 1.5170153, 0.99538457, 0.8219381, 0.63958292, 0.29008701, -0.065867502, -0.075335519, -0.20866915, -0.5007413, -0.41176548, -0.19051229, -1.0286497, 0.28536774, -0.43596889, -0.030019219, -0.83504908, -0.05208197, -0.6009845, -0.23809315, -0.4464516, -0.0015396437, -0.014180638, -0.74741393, -0.26661983, -0.29343086, 0.57945583, 0.33302771, 0.17031379, 0.21408334, 0.13742589, 0.30361295, 0.12269426, -0.035974672, -0.36869103, 0.26317275, -0.23912207, -0.26135019, 0.019544645, -0.21131887, -0.66129613, -0.78018257, -0.76818114, -1.414147, -0.86316933, -1.2760757, -1.2975309, -1.588528, -1.6910553, -1.2603052, -1.1384049, -2.2562622, -1.5232889, -1.1348859, -1.5521593, -0.59882458, -0.5316139, -0.66205147};
		RegressionGeneration r1 = null;
		StringBuilder stringBuilder = new StringBuilder();
		for(int i = 0; i < 100; i++){
			r1 = new RegressionGeneration(10, 1, 0, 1, 1, new RegressionFunctions(), input, output);
			stringBuilder.append(r1.run());
			stringBuilder.append("\n");
		}
		PrintWriter writer = null;
		try{
			writer = new PrintWriter("results-one-row.csv");

		}
		catch(Exception e){

		}
		String results = stringBuilder.toString();
		writer.println(results);
		writer.close();

//
//		for(int i = -10; i < 11; i++){
//			System.out.println(i + "\t" + i*i);
		}*/
		ArrayList<ArrayList<ArrayList<Integer>>> graphs = new ArrayList<>();
		ArrayList<ArrayList<Edge>> edges = new ArrayList<>();
		File folder = new File("datasets");
		File[] listOfFiles = folder.listFiles();
		Integer[] outputs = new Integer[listOfFiles.length];

		for (int i = 0; i < listOfFiles.length; i++) {
			VCPInstance v = new VCPInstance(new File("datasets/" + listOfFiles[i].getName()));
			graphs.add(v.getGraph());
			edges.add(v.getEdges());
			outputs[i] = v.getMinVCSize();
		}

		GenericFunction[] functions = {new FunctionAddU(), new FunctionAddV(), new FunctionAnd(), new FunctionBreak(),
				new FunctionElse(), new FunctionEndIf(), new FunctionIfDegVEqualsDegU(), new FunctionIfDegVGTDegU(),
				new FunctionIfDegVLTDegU(), new FunctionIfEquiprobability(), new FunctionIfVGTU(), new FunctionIfVLTU(),
				new FunctionIfVNotInCover(), new FunctionOr()
		};
		VCPGeneration generation = new VCPGeneration(20,4,0,1,functions, graphs,
				edges, outputs, 10000);

		generation.run();

		generation.getBestScoreOverall();

//		ArrayList<ArrayList<Integer>> graph = new ArrayList<>();
//		ArrayList<Edge> edges = new ArrayList<>();
//		List<String> lines = null;
//		File file = new File(args[0]);
//		Scanner sc = null;
//		try{
//			sc = new Scanner(file);
//		}
//		catch(Exception e){
//			e.printStackTrace();
//		}
//		while (sc.hasNextLine()){
//			if(sc.nextLine().charAt(0) == 'p'){
//				sc.nextLine();
//				continue;
//			}
//			System.out.println(sc.nextLine());
//		}

	}
}