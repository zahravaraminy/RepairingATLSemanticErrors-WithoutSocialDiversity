package jmetal.problems;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CoSolutionThreeStep;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.Setting;
import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.encodings.solutionType.IntSolutionTypeThreeStep;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import produce.output.xmimodel.LaunchATLHelper;

public class MyProblemThreeStep extends Problem {
	int lower_rules_size = 5;
	int upper_rules_size = 10;
	public static CoSolutionThreeStep S2;
	public static boolean findfirst=false;
	public static int oldoperation1 = 0;
	public static int replaceoperation1 = 0;
	public static int secondoldoperation1 = 0;
	public static int oldoperation2 = 0;
	public static int replaceoperation2 = 0;
	public static int secondoldoperation2 = 0;
	public static int oldoperation3 = 0;
	public static int replaceoperation3 = 0;
	public static int secondoldoperation3 = 0;
	public static int oldoperation4 = 0;
	public static int replaceoperation4 = 0;
	public static int secondoldoperation4 = 0;
	public static int oldoperation5 = 0;
	public static int replaceoperation5 = 0;
	public static int secondoldoperation5 = 0;
	public static int oldoperation6 = 0;
	public static int replaceoperation6 = 0;
	public static int secondoldoperation6 = 0;
	public static int oldoperation7 = 0;
	public static int replaceoperation7 = 0;
	public static int secondoldoperation7 = 0;
	public static int oldoperation8 = 0;
	public static int replaceoperation8 = 0;
	public static int secondoldoperation8 = 0;
	public static int oldoperation9 = 0;
	public static int replaceoperation9 = 0;
	public static int secondoldoperation9 = 0;
	public static int oldoperation10 = 0;
	public static int replaceoperation10 = 0;
	public static int secondoldoperation10 = 0;
	public static int oldoperation11 = 0;
	public static int replaceoperation11 = 0;
	public static int secondoldoperation11 = 0;
	public static int oldoperation12 = 0;
	public static int replaceoperation12 = 0;
	public static int secondoldoperation12 = 0;
	public static int oldoperation13 = 0;
	public static int replaceoperation13 = 0;
	public static int secondoldoperation13 = 0;
	public static int oldoperation14 = 0;
	public static int replaceoperation14 = 0;
	public static int secondoldoperation14 = 0;
	public static int oldoperation15 = 0;
	public static int replaceoperation15 = 0;
	public static int secondoldoperation15 = 0;
	public static int oldoperation16 = 0;
	public static int replaceoperation16 = 0;
	public static int secondoldoperation16 = 0;
	public static int oldoperation17 = 0;
	public static int replaceoperation17 = 0;
	public static int secondoldoperation17 = 0;
	public static int oldoperation18 = 0;
	public static int replaceoperation18 = 0;
	public static int secondoldoperation18 = 0;
	public static int oldoperation19 = 0;
	public static int replaceoperation19 = 0;
	public static int secondoldoperation19 = 0;
	public static int oldoperation20 = 0;
	public static int replaceoperation20 = 0;
	public static int secondoldoperation20 = 0;
	public static int oldoperation21 = 0;
	public static int replaceoperation21 = 0;
	public static int secondoldoperation21 = 0;
	public static int oldoperation22 = 0;
	public static int replaceoperation22 = 0;
	public static int secondoldoperation22 = 0;
	public static int indexoperation = -1;
	public static Solution repeatoperation;
	public static ArrayList<Integer> listold;	
	int applyoperation = 0;
	int sumfirstfit=0;
	int sumsecondfit=0;
	int sumthirdfit=0;
	public MyProblemThreeStep(String solutionType) throws ClassNotFoundException {
		this(solutionType, 1);

	} // Adapt_Interface
    public int getsumfirstfit() {
    	return this.sumfirstfit;
    }
    public int getsumsecondfit() {
    	return this.sumsecondfit;
    }
    public int getsumthirdfit() {
    	return this.sumthirdfit;
    }
    public void setsumfirstfit(int i) {
    	this.sumfirstfit=i;
    }
    public void setsumsecondfit(int i) {
    	this.sumsecondfit=i;
    }
    public void setsumthirdfit(int i) {
    	this.sumthirdfit=i;
    }
	public MyProblemThreeStep(String solutionType, Integer numberOfVariables) {
		
		numberOfVariables_ = 8; 
		numberOfObjectives_ = 3;
		numberOfConstraints_ = 0;
		problemName_ = "Transformation_Coevolution";
		lowerLimit_ = new double[numberOfVariables_];
		upperLimit_ = new double[numberOfVariables_];
		for (int i = 0; i < numberOfVariables; i++) {
			lowerLimit_[i] = IntSolutionTypeThreeStep.min_operations_size;// lower_rules_size;
			upperLimit_[i] = IntSolutionTypeThreeStep.min_operations_size; // upper_rules_size;
		}
		if (solutionType.compareTo("Int") == 0) {
			
			solutionType_ = new IntSolutionTypeThreeStep(this);
		} else {
			System.out.println("Error: solution type " + solutionType + " invalid");
			System.exit(-1);
		}
		
	}

	public List<EPackage> retPackMM(Resource resourceMM) {
		ResourceSet resourceSet = resourceMM.getResourceSet();
		List<EPackage> metamodel = new ArrayList<EPackage>();
		for (EObject obj : resourceMM.getContents()) {
			if (obj instanceof EPackage) {
				EPackage.Registry.INSTANCE.put(((EPackage) obj).getNsURI(),
						((EPackage) obj).getEFactoryInstance().getEPackage());
				resourceSet.getPackageRegistry().put(((EPackage) obj).getNsURI(),
						((EPackage) obj).getEFactoryInstance().getEPackage());
				metamodel.add((EPackage) obj);
			}
		}
		return metamodel;
	}

	public Resource retPackResouceMM(String MMPath) {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore",
				new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);// ecore.getFullPath().toOSString());
		Resource resource = resourceSet.getResource(fileURI, true);
		return resource;
	}

	private /* static */ long index = 1;

	private String getValidNameOfFile(String outputFolder) {
		String outputfile = null;
		String aux = null;
		aux = File.separatorChar + "finalresult" + Class2Rel.totalnumber + ".atl";
		outputfile = outputFolder + aux;
		return outputfile;
	}

	protected boolean save(EMFModel atlModel, String outputFolder) {
		try {
			// save atl file
			String atl_transformation = this.getValidNameOfFile(outputFolder);
			AtlParser atlParser = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);

			// compile transformation
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} catch (ATLCoreException e) {
		}
		

		return false;
	}

	@Override
	public void evaluate(Solution solution,FileWriter csvWriterpareto,LaunchATLHelper atlLauncher) throws Exception,NullPointerException {

		CoSolutionThreeStep S = solution.getCoSolutionThreeStep();
		S2 = S;
		CommonFunctionOperators cp=new CommonFunctionOperators();
		List<Object> comments = null;
		Setting s=new Setting();
		
		if (NSGAIIThreeStep.counter < s.getpopulationsize()) {
			
			update3VariableforCrossOver(solution);
			listold = new ArrayList<Integer>();
			UpdatePreRunningAttributes();
			solution=SetListfirstandsecondindices(solution);
			
			 for (int i = 0; i < IntSolutionTypeThreeStep.operations.size(); i++) {
				 
				indexoperation = i + 1;
				this.applyoperation = NSGAIIThreeStep.numop;
				int numop= NSGAIIThreeStep.numop;
				comments = S.getOp().executeOperations(IntSolutionTypeThreeStep.operations.get(i), S,
						IntSolutionTypeThreeStep.operations.size(), solution,this.sumfirstfit,this.sumsecondfit,this.sumthirdfit,csvWriterpareto,cp,atlLauncher);			
				AddCommentofNewOperationToSolution(comments,solution);	
				// first add operation but cannot find parameter to apply operation, so it means do not run operation
				if(numop==NSGAIIThreeStep.numop)
					IntSolutionTypeThreeStep.operations.set(i, -3);
				boolean ch=false;
				ch=IfAddNewOperationstoExistingOperations(ch, solution,i);
				
			 	}
			
			 	solution=update3VariableSolution(solution);
		} else {
			update3VariableforCrossOver( solution);
			UpdatePreRunningAttributes();
			solution=SetListfirstandsecondindices(solution);
				
			for (int i = 0; i < solution.getoperations().size(); i++) {
				indexoperation = i + 1;
				int numop=NSGAIIThreeStep.numop;
				comments = S.getOp().executeOperations(solution.getoperations().get(i), S,
						solution.getoperations().size(), solution,this.sumfirstfit,this.sumsecondfit,this.sumthirdfit,csvWriterpareto,cp, atlLauncher);		
				AddCommentofNewOperationToSolution(comments,solution);
				if(numop==NSGAIIThreeStep.numop)
					solution.getoperations().set(i, -3);
				boolean ch=false;
				ch=IfAddNewOperationstoExistingOperations(ch, solution,i);
				if(NSGAIIThreeStep.mandatoryoutpatternoperation.size()>0 && (i+1)<=IntSolutionTypeThreeStep.max_operations_size)
					solution.getoperations().set(i+1, 1);	
				
			}
		
		}    
		AddOneSolutionParameters(solution,S);
		Class2Rel.totalnumber = Class2Rel.totalnumber + 1;
		
	}

	private boolean IfAddNewOperationstoExistingOperations(boolean ch, Solution solution, int  i) {
		
		if(NSGAIIThreeStep.mandatorycreationoperation.size()>0 && MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size()<=IntSolutionTypeThreeStep.max_operations_size
				) {
			ch=true;
			solution.getoperations().set(i+1, 8);	
		}
		if(ch==false && NSGAIIThreeStep.mandatorydeletionattribute.size()>0 && MyProblemThreeStep.indexoperation+NSGAIIThreeStep.mandatorycreationoperation.size()+NSGAIIThreeStep.mandatorydeletionattribute.size()<=IntSolutionTypeThreeStep.max_operations_size )
			solution.getoperations().set(i+1, 11);	
		
		return ch;
	}
	private void AddCommentofNewOperationToSolution(List<Object> comments, Solution solution) {
		
		if(comments!=null) {
			
			if ( comments.size() == 3 && IntSolutionTypeThreeStep.operations.size()!=MyProblemThreeStep.indexoperation) {
				solution.totalcomments.add((String) comments.get(2));	
			}
			
		if (comments.size() == 6) {
			solution.totalcomments.add((String) comments.get(2));
			
		}	
	}
		
	}
	private void UpdatePreRunningAttributes() {
		
		NSGAIIThreeStep.numop = 0;
		NSGAIIThreeStep.deletlist.clear();
		NSGAIIThreeStep.deletlist = new ArrayList<Integer>();
		NSGAIIThreeStep.listoutpatternmodify.clear();
		NSGAIIThreeStep.listoutpatternmodify = new ArrayList<Integer>();
		NSGAIIThreeStep.counterdelet = 0;
		
	}
	private void AddOneSolutionParameters(Solution solution, CoSolutionThreeStep S) {
		double[] f = new double[numberOfObjectives_];
		solution=update3VariableSolution( solution);
		initial3value();
		solution.setindex(Class2Rel.totalnumber);
		solution.setcomments2(solution.totalcomments);	                               
		if(NSGAIIThreeStep.numop==0)
			NSGAIIThreeStep.numop=NSGAIIThreeStep.numop+2;
		f[1] = NSGAIIThreeStep.numop;
		solution.setsecondfitness( f[1]);
		
		f[0] = NSGAIIThreeStep.fitness2;
		solution.setfirstfitness(NSGAIIThreeStep.fitness2);
		if(NSGAIIThreeStep.fitness2==0 && MyProblemThreeStep.findfirst==false)
		{
			MyProblemThreeStep.findfirst=true;
			NSGAIIThreeStep.fixedgeneration=Class2Rel.totalnumber;
		}
		f[2] = NSGAIIThreeStep.fitness3;
		solution.setfitnessthird( f[2] );
		solution.setCoSolutionThreeStep(S);
		solution.setObjective(0, f[0]);
		solution.setObjective(1, f[1]);
		solution.setObjective(2, f[2]);
		solution.RHSattributerefinement.clear();
		solution.RHSattributelocation.clear();	
		
	}
	private void initial3value() {
		// TODO Auto-generated method stub
		
		oldoperation4 = 0;
		replaceoperation4 = 0;
		secondoldoperation4 = 0;
		oldoperation3 = 0;
		replaceoperation3 = 0;
		secondoldoperation3 = 0;
		oldoperation2 = 0;
		replaceoperation2 = 0;
		secondoldoperation2 = 0;
		oldoperation1 = 0;
		replaceoperation1 = 0;
		secondoldoperation1 = 0;
		oldoperation5 = 0;
		replaceoperation5 = 0;
		secondoldoperation5 = 0;
		oldoperation6 = 0;
		replaceoperation6 = 0;
		secondoldoperation6 = 0;
		oldoperation7 = 0;
		replaceoperation7 = 0;
		secondoldoperation7 = 0;
		oldoperation8 = 0;
		replaceoperation8 = 0;
		secondoldoperation8 = 0;
		oldoperation9 = 0;
		replaceoperation9 = 0;
		secondoldoperation9 = 0;
		oldoperation10 = 0;
		replaceoperation10 = 0;
		secondoldoperation10 = 0;
		oldoperation11 = 0;
		replaceoperation11 = 0;
		secondoldoperation11 = 0;
		oldoperation12 = 0;
		replaceoperation12 = 0;
		secondoldoperation12 = 0;
		oldoperation13 = 0;
		replaceoperation13 = 0;
		secondoldoperation13 = 0;
		oldoperation14 = 0;
		replaceoperation14 = 0;
		secondoldoperation14 = 0;
		oldoperation15 = 0;
		replaceoperation15 = 0;
		secondoldoperation15 = 0;
		oldoperation16 = 0;
		replaceoperation16 = 0;
		secondoldoperation16 = 0;
		oldoperation17 = 0;
		replaceoperation17 = 0;
		secondoldoperation17 = 0;
		oldoperation18 = 0;
		replaceoperation18 = 0;
		secondoldoperation18 = 0;
		oldoperation19 = 0;
		replaceoperation19 = 0;
		secondoldoperation19 = 0;
		oldoperation20 = 0;
		replaceoperation20 = 0;
		secondoldoperation20 = 0;
		oldoperation21 = 0;
		replaceoperation21 = 0;
		secondoldoperation21 = 0;
		oldoperation22 = 0;
		replaceoperation22 = 0;
		secondoldoperation22 = 0;

		
	}
	private Solution update3VariableSolution(Solution solution) {
		
		solution.setoldrandomIntoperation4(oldoperation4);
		solution.setreplacerandomIntoperation4(replaceoperation4);
		solution.setsecondoldrandomIntoperation4(secondoldoperation4);
		solution.setoldrandomIntoperation3(oldoperation3);
		solution.setreplacerandomIntoperation3(replaceoperation3);
		solution.setsecondoldrandomIntoperation3(secondoldoperation3);
		solution.setoldrandomIntoperation2(oldoperation2);
		solution.setreplacerandomIntoperation2(replaceoperation2);
		solution.setsecondoldrandomIntoperation2(secondoldoperation2);
		solution.setoldrandomIntoperation1(oldoperation1);
		solution.setreplacerandomIntoperation1(replaceoperation1);
		solution.setsecondoldrandomIntoperation1(secondoldoperation1);
		solution.setoldrandomIntoperation5(oldoperation5);
		solution.setreplacerandomIntoperation5(replaceoperation5);
		solution.setsecondoldrandomIntoperation5(secondoldoperation5);
		solution.setoldrandomIntoperation6(oldoperation6);
		solution.setreplacerandomIntoperation6(replaceoperation6);
		solution.setsecondoldrandomIntoperation6(secondoldoperation6);
		solution.setoldrandomIntoperation7(oldoperation7);
		solution.setreplacerandomIntoperation7(replaceoperation7);
		solution.setsecondoldrandomIntoperation7(secondoldoperation7);
		solution.setoldrandomIntoperation8(oldoperation8);
		solution.setreplacerandomIntoperation8(replaceoperation8);
		solution.setsecondoldrandomIntoperation8(secondoldoperation8);
		solution.setoldrandomIntoperation9(oldoperation9);
		solution.setreplacerandomIntoperation9(replaceoperation9);
		solution.setsecondoldrandomIntoperation9(secondoldoperation9);
		solution.setoldrandomIntoperation10(oldoperation10);
		solution.setreplacerandomIntoperation10(replaceoperation10);
		solution.setsecondoldrandomIntoperation10(secondoldoperation10);
		solution.setoldrandomIntoperation11(oldoperation11);
		solution.setreplacerandomIntoperation11(replaceoperation11);
		solution.setsecondoldrandomIntoperation11(secondoldoperation11);
		solution.setoldrandomIntoperation12(oldoperation12);
		solution.setreplacerandomIntoperation12(replaceoperation12);
		solution.setsecondoldrandomIntoperation12(secondoldoperation12);
		solution.setoldrandomIntoperation13(oldoperation13);
		solution.setreplacerandomIntoperation13(replaceoperation13);
		solution.setsecondoldrandomIntoperation13(secondoldoperation13);
		solution.setoldrandomIntoperation14(oldoperation14);
		solution.setreplacerandomIntoperation14(replaceoperation14);
		solution.setsecondoldrandomIntoperation14(secondoldoperation14);
		solution.setoldrandomIntoperation15(oldoperation15);
		solution.setreplacerandomIntoperation15(replaceoperation15);
		solution.setsecondoldrandomIntoperation15(secondoldoperation15);
		solution.setoldrandomIntoperation16(oldoperation16);
		solution.setreplacerandomIntoperation16(replaceoperation16);
		solution.setsecondoldrandomIntoperation16(secondoldoperation16);
		solution.setoldrandomIntoperation17(oldoperation17);
		solution.setreplacerandomIntoperation17(replaceoperation17);
		solution.setsecondoldrandomIntoperation17(secondoldoperation17);
		solution.setoldrandomIntoperation18(oldoperation18);
		solution.setreplacerandomIntoperation18(replaceoperation18);
		solution.setsecondoldrandomIntoperation18(secondoldoperation18);
		solution.setoldrandomIntoperation19(oldoperation19);
		solution.setreplacerandomIntoperation19(replaceoperation19);
		solution.setsecondoldrandomIntoperation19(secondoldoperation19);
		solution.setoldrandomIntoperation20(oldoperation20);
		solution.setreplacerandomIntoperation20(replaceoperation20);
		solution.setsecondoldrandomIntoperation20(secondoldoperation20);
		solution.setoldrandomIntoperation21(oldoperation21);
		solution.setreplacerandomIntoperation21(replaceoperation21);
		solution.setsecondoldrandomIntoperation21(secondoldoperation21);
		solution.setoldrandomIntoperation22(oldoperation22);
		solution.setreplacerandomIntoperation22(replaceoperation22);
		solution.setsecondoldrandomIntoperation22(secondoldoperation22);

		return solution;
	}
	private void update3VariableforCrossOver(Solution solution) {
	
		
		oldoperation4 = solution.getoldrandomIntoperation4();
		replaceoperation4 = solution.getreplacerandomIntoperation4();
		secondoldoperation4 = solution.getsecondoldrandomIntoperation4();
		oldoperation3 = solution.getoldrandomIntoperation3();
		replaceoperation3 = solution.getreplacerandomIntoperation3();
		secondoldoperation3 = solution.getsecondoldrandomIntoperation3();
		oldoperation2 = solution.getoldrandomIntoperation2();
		replaceoperation2 = solution.getreplacerandomIntoperation2();
		secondoldoperation2 = solution.getsecondoldrandomIntoperation2();
		oldoperation1 = solution.getoldrandomIntoperation1();
		replaceoperation1 = solution.getreplacerandomIntoperation1();
		secondoldoperation1 = solution.getsecondoldrandomIntoperation1();
		oldoperation5 = solution.getoldrandomIntoperation5();
		replaceoperation5 = solution.getreplacerandomIntoperation5();
		secondoldoperation5 = solution.getsecondoldrandomIntoperation5();
		oldoperation6 = solution.getoldrandomIntoperation6();
		replaceoperation6 = solution.getreplacerandomIntoperation6();
		secondoldoperation6 = solution.getsecondoldrandomIntoperation6();
		oldoperation7 = solution.getoldrandomIntoperation7();
		replaceoperation7 = solution.getreplacerandomIntoperation7();
		secondoldoperation7 = solution.getsecondoldrandomIntoperation7();
		oldoperation8 = solution.getoldrandomIntoperation8();
		replaceoperation8 = solution.getreplacerandomIntoperation8();
		secondoldoperation8 = solution.getsecondoldrandomIntoperation8();
		oldoperation9 = solution.getoldrandomIntoperation9();
		replaceoperation9 = solution.getreplacerandomIntoperation9();
		secondoldoperation9 = solution.getsecondoldrandomIntoperation9();
		oldoperation10 = solution.getoldrandomIntoperation10();
		replaceoperation10 = solution.getreplacerandomIntoperation10();
		secondoldoperation10 = solution.getsecondoldrandomIntoperation10();
		oldoperation11 = solution.getoldrandomIntoperation11();
		replaceoperation11 = solution.getreplacerandomIntoperation11();
		secondoldoperation11 = solution.getsecondoldrandomIntoperation11();
		oldoperation12 = solution.getoldrandomIntoperation12();
		replaceoperation12 = solution.getreplacerandomIntoperation12();
		secondoldoperation12 = solution.getsecondoldrandomIntoperation12();
		oldoperation13 = solution.getoldrandomIntoperation13();
		replaceoperation13 = solution.getreplacerandomIntoperation13();
		secondoldoperation13 = solution.getsecondoldrandomIntoperation13();
		oldoperation14 = solution.getoldrandomIntoperation14();
		replaceoperation14 = solution.getreplacerandomIntoperation14();
		secondoldoperation14 = solution.getsecondoldrandomIntoperation14();
		oldoperation15 = solution.getoldrandomIntoperation15();
		replaceoperation15 = solution.getreplacerandomIntoperation15();
		secondoldoperation15 = solution.getsecondoldrandomIntoperation15();
		oldoperation16 = solution.getoldrandomIntoperation16();
		replaceoperation16 = solution.getreplacerandomIntoperation16();
		secondoldoperation16 = solution.getsecondoldrandomIntoperation16();
		oldoperation17 = solution.getoldrandomIntoperation17();
		replaceoperation17 = solution.getreplacerandomIntoperation17();
		secondoldoperation17 = solution.getsecondoldrandomIntoperation17();
		oldoperation18 = solution.getoldrandomIntoperation18();
		replaceoperation18 = solution.getreplacerandomIntoperation18();
		secondoldoperation18 = solution.getsecondoldrandomIntoperation18();
		oldoperation19 = solution.getoldrandomIntoperation19();
		replaceoperation19 = solution.getreplacerandomIntoperation19();
		secondoldoperation19 = solution.getsecondoldrandomIntoperation19();
		oldoperation20 = solution.getoldrandomIntoperation20();
		replaceoperation20 = solution.getreplacerandomIntoperation20();
		secondoldoperation20 = solution.getsecondoldrandomIntoperation20();
		oldoperation21 = solution.getoldrandomIntoperation21();
		replaceoperation21 = solution.getreplacerandomIntoperation21();
		secondoldoperation21 = solution.getsecondoldrandomIntoperation21();
		oldoperation22 = solution.getoldrandomIntoperation22();
		replaceoperation22 = solution.getreplacerandomIntoperation22();
		secondoldoperation22 = solution.getsecondoldrandomIntoperation22();

	}
	private Solution SetListfirstandsecondindices(Solution solution) {
		
		solution.listfirstindices.add( oldoperation1);
		solution.listfirstindices.add( oldoperation2);
		solution.listfirstindices.add( oldoperation3);
		solution.listfirstindices.add( oldoperation4);
		solution.listfirstindices.add( oldoperation5);
		solution.listfirstindices.add( oldoperation6);
		solution.listfirstindices.add( oldoperation7);
		solution.listfirstindices.add( oldoperation8);
		solution.listfirstindices.add( oldoperation9);
		solution.listfirstindices.add( oldoperation10);
		solution.listfirstindices.add( oldoperation11);
		solution.listfirstindices.add( oldoperation12);
		solution.listfirstindices.add( oldoperation13);
		solution.listfirstindices.add( oldoperation14);
		solution.listfirstindices.add( oldoperation15);
		solution.listsecondindices.add(replaceoperation1 );
		solution.listsecondindices.add(replaceoperation2 );
		solution.listsecondindices.add(replaceoperation3 );
		solution.listsecondindices.add(replaceoperation4 );
		solution.listsecondindices.add(replaceoperation5 );
		solution.listsecondindices.add(replaceoperation6 );
		solution.listsecondindices.add(replaceoperation7 );
		solution.listsecondindices.add(replaceoperation8 );
		solution.listsecondindices.add(replaceoperation9 );
		solution.listsecondindices.add(replaceoperation10 );
		solution.listsecondindices.add(replaceoperation11 );
		solution.listsecondindices.add(replaceoperation12 );
		solution.listsecondindices.add(replaceoperation13 );
		solution.listsecondindices.add(replaceoperation14 );
		solution.listsecondindices.add(replaceoperation15 );
		NSGAIIThreeStep.mandatoryoutpatternoperation.clear();
		NSGAIIThreeStep.mandatoryoutpatternlocation.clear();
		NSGAIIThreeStep.mandatorycreationoperation.clear();
		NSGAIIThreeStep.mandatorycreationlocation.clear();
		NSGAIIThreeStep.mandatorydeletionattribute.clear();
		NSGAIIThreeStep.mandatorydeletionlocation.clear();
		NSGAIIThreeStep.mandatoryoutpatternoperation = new ArrayList<String>();
		NSGAIIThreeStep.mandatoryoutpatternlocation = new ArrayList<Integer>();
		NSGAIIThreeStep.mandatorycreationoperation = new ArrayList<String>();
		NSGAIIThreeStep.mandatorycreationlocation = new ArrayList<Integer>();
		NSGAIIThreeStep.mandatorydeletionattribute = new ArrayList<String>();
		NSGAIIThreeStep.mandatorydeletionlocation = new ArrayList<Integer>();
		return solution;
	}
	protected void save2(EMFModel atlModel, String outputFolder) {
		try {
			// save atl file
			String aux = File.separatorChar + "finalresult" + Class2Rel.totalnumber + ".atl";
			String atl_transformation = outputFolder + aux;
			AtlParser atlParser = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);
		} catch (ATLCoreException e) {
		}

	}

}
