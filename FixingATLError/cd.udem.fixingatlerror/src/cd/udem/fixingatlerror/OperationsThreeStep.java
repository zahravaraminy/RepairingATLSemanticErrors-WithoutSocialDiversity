package cd.udem.fixingatlerror;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.xmi.IllegalValueException;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.core.ATLCoreException;
import org.eclipse.m2m.atl.core.ModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFInjector;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import org.eclipse.m2m.atl.core.emf.EMFModelFactory;
import org.eclipse.m2m.atl.core.emf.EMFReferenceModel;
import org.eclipse.m2m.atl.engine.parser.AtlParser;
import jmetal.core.Solution;
import jmetal.metaheuristics.nsgaII.NSGAII;
import jmetal.metaheuristics.nsgaII.NSGAIIThreeStep;
import jmetal.metaheuristics.nsgaII.NSGAIIpostprocessing;
import jmetal.problems.MyProblemThreeStep;
import transML.exceptions.transException;
import witness.generator.MetaModel;
import anatlyzer.atl.analyser.Analyser;
import anatlyzer.atlext.ATL.MatchedRule;
import anatlyzer.atlext.ATL.OutPatternElement;
import anatlyzer.atlext.ATL.Rule;
import evaluation.mutator.AbstractMutator;
import creation.operator.BindingCreationMutatorThreeStep;
import deletion.operator.BindingDeletionMutator;
import modification.feature.operator.BindingModificationMutatorThreeStep;
import modification.feature.operator.NavigationModificationMutatorThreeStep;
import modification.invocation.operator.CollectionOperationModificationMutatorThreeStep;
import modification.invocation.operator.abstractclass.IteratorModificationMutatorThreeStep;
import modification.invocation.operator.PredefinedOperationModificationMutatorThreeStep;
import modification.type.operator.abstractclass.ArgumentModificationMutatorThreeStep;
import modification.type.operator.abstractclass.CollectionModificationMutatorThreeStep;
import modification.type.operator.abstractclass.InElementModificationMutatorThreeStep;
import modification.type.operator.abstractclass.OutElementModificationMutatorThreeStep;
import produce.output.xmimodel.LaunchATLHelper;
import anatlyzer.atl.model.ATLModel;
public class OperationsThreeStep extends BaseTest {

	public static final ArrayList<Double> probability_outpattern = new ArrayList<Double>();
	public static ArrayList<ArrayList<String>> listpropertyname = new ArrayList<ArrayList<String>>();
	public static  ArrayList<ArrayList<String>> listpropertytype = new ArrayList<ArrayList<String>>();
	public ArrayList<ArrayList<Integer>> listlowerboundcardinality = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> listupperboundcardinality = new ArrayList<ArrayList<Integer>>();
	public ArrayList<ArrayList<Integer>> listpropertynamelocation = new ArrayList<ArrayList<Integer>>();
	public ArrayList<String> filterSolution = new ArrayList<String>();
	public static String statecase=null; 
    public ArrayList<ArrayList<Integer>> originalwrapper = new ArrayList<ArrayList<Integer>>();
	ATLModel model;
	Analyser analyser;
	ArrayList<MatchedRule> L;
	public ATLModel wrapper;
	public EMFModel atlModel;         // model of the original transformation
	public static ATLModel  lastmodel;         // model of the original transformation
	public static EMFModel emfModel; 
	public static String str;
	private MetaModel iMetaModel = null, oMetaModel = null;
	int populationsize=100;
	double populationsizedouble=100.0;
	// temporal folders
	private String folderMutants;
	
	
	private EMFModel loadTransformationModel (String atlTransformationFile) throws ATLCoreException {
		ModelFactory      modelFactory = new EMFModelFactory();
		
		EMFReferenceModel atlMetamodel = null;
		if (!Platform.isRunning()) {
			EMFInjector emfinjector = new EMFInjector();
			atlMetamodel = (EMFReferenceModel)modelFactory.newReferenceModel();
			emfinjector.inject(atlMetamodel, NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/ATL.ecore");
		}
		else {
			atlMetamodel = (EMFReferenceModel)modelFactory.getBuiltInResource(NSGAIIThreeStep.startingroot+"/varaminz/"
					+NSGAIIThreeStep.step+"/examples/ATL.ecore");  	
		}
		AtlParser         atlParser    = new AtlParser();		
		EMFModel          atlModel     = (EMFModel)modelFactory.newModel(atlMetamodel);
		atlParser.inject (atlModel, atlTransformationFile);	
		atlModel.setIsTarget(true);				
		return atlModel;
	}
	public List<EPackage> retPackMM(Resource resourceMM)
	 {
		ResourceSet resourceSet=resourceMM.getResourceSet();
		List<EPackage> metamodel = new ArrayList<EPackage>();
		for (EObject obj : resourceMM.getContents()) {
			if (obj instanceof EPackage) {
				EPackage.Registry.INSTANCE.put		(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
				resourceSet.getPackageRegistry().put(((EPackage)obj).getNsURI(), ((EPackage)obj).getEFactoryInstance().getEPackage());
				metamodel.add((EPackage)obj);
			}
		}
		return metamodel;
	 }
	public Resource retPackResouceMM(String MMPath)
	 {	 	
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		URI fileURI = URI.createFileURI(MMPath);//ecore.getFullPath().toOSString());		
		Resource resource = resourceSet.getResource(fileURI, true);	
		return resource;
	 }


	public void Testeroperate (String trafo, String temporalFolder) throws ATLCoreException, transException {
		
		Setting s=new Setting();
		this.atlModel = this.loadTransformationModel(trafo);
		this.wrapper = new ATLModel(this.atlModel.getResource());
		this.folderMutants = temporalFolder + "mutants" + File.separator;
		//For PNML
		 final String MMRootPath     = s.gettargetmetamodel();
		 List<EPackage>   metamodels= retPackMM(retPackResouceMM(MMRootPath));
			for (EPackage p: metamodels) {
				 this.iMetaModel = new MetaModel(p); 
				 }
			//for PetriNet
			final String MMRootPath2     = s.getsourcemetamodel();	
			List<EPackage>   metamodels2= retPackMM(retPackResouceMM(MMRootPath2));
			 for (EPackage p: metamodels2) {
				 this.oMetaModel = new MetaModel(p); 
				
			 }
		  this.iMetaModel.setName (s.getfirstecorename());
		  this.oMetaModel.setName (s.getsecondecorename());
			ExtractPropertyname(this.wrapper);
			
	}
	
	public Resource createModel(String path, ResourceSet rs) {
		URI fileURI = URI.createFileURI(path);
        Resource resource = rs.createResource( fileURI);
        return resource;
    }


	public Resource loadModel(String path, ResourceSet rs) {
		URI fileURI = URI.createFileURI(path);
		Resource resource = rs.getResource(fileURI  , true);
        return resource;
    }

	protected String toString (EObject element) {
		String toString = "";
		EPackage pack   = element.eClass().getEPackage();
		if (pack.getEClassifier("OclModelElement")!=null && pack.getEClassifier("OclModelElement").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("name");
			toString = element.eGet(feature).toString();
		}
		else if (pack.getEClassifier("StringType")!=null && pack.getEClassifier("StringType").isInstance(element)) {
			toString = "String";
		}
		else if (pack.getEClassifier("BooleanType")!=null && pack.getEClassifier("BooleanType").isInstance(element)) {
			toString = "Booelan";
		}
		else if (pack.getEClassifier("IntegerType")!=null && pack.getEClassifier("IntegerType").isInstance(element)) {
			toString = "Integer";
		}
		else if (pack.getEClassifier("FloatType")!=null && pack.getEClassifier("FloatType").isInstance(element)) {
			toString = "Float";
		}
		else if (pack.getEClassifier("CollectionType")!=null && pack.getEClassifier("CollectionType").isInstance(element)) {
			toString = element.eClass().getName();
		}
		else if (pack.getEClassifier("CollectionExp")!=null && pack.getEClassifier("CollectionExp").isInstance(element)) {
			toString = element.eClass().getName();
		}
		else if (pack.getEClassifier("VariableExp")!=null && pack.getEClassifier("VariableExp").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("referredVariable");
			toString = toString((EObject)element.eGet(feature));
		}
		else if (pack.getEClassifier("InPatternElement")!=null && pack.getEClassifier("InPatternElement").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("varName");
			toString = element.eGet(feature).toString();
		}
		else if (pack.getEClassifier("Rule")!=null && pack.getEClassifier("Rule").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("name");
			toString = element.eGet(feature).toString();
		}
		else if (pack.getEClassifier("NavigationOrAttributeCallExp")!=null && pack.getEClassifier("NavigationOrAttributeCallExp").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("name");
			toString = element.eGet(feature).toString();
		}
		else if (pack.getEClassifier("Iterator")!=null && pack.getEClassifier("Iterator").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("varName");
			toString = element.eGet(feature).toString();
		}
		else if (pack.getEClassifier("VariableDeclaration")!=null && pack.getEClassifier("VariableDeclaration").isInstance(element)) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("varName");
			toString = element.eGet(feature).toString();
		}
		return toString;
	}

	private void ExtractPropertyname(ATLModel wrapper) {
		
		int row=0;
		ClearArrayLists();
		 ArrayList<String> row4 = new ArrayList<String>();
		int  indexoutpattern=0;
		
		for (Rule rule : (List<Rule>)wrapper.allObjectsOf(Rule.class)) {
			for (OutPatternElement outElement : rule.getOutPattern().getElements()) {
				
				ArrayList<String> row2 = new ArrayList<String>();			
				ArrayList<String> row6 = new ArrayList<String>();
				ArrayList<Integer> row7 = new ArrayList<Integer>();
				ArrayList<Integer> row8 = new ArrayList<Integer>();
				ArrayList<Integer> row3 = new ArrayList<Integer>();
				
				EObject oldFeatureValue3= SpecificOutPattern(wrapper, indexoutpattern); 
				indexoutpattern=indexoutpattern+1;
				ArrayList<Integer> row5 = new ArrayList<Integer>();
				row4.add(toString(oldFeatureValue3));
				int index=NSGAIIThreeStep.classnamestringtarget.indexOf( toString(oldFeatureValue3));
				
				for(int j=0;j<outElement.getBindings().size();j++) 
				{
					row2.add(outElement.getBindings().get(j).getPropertyName());
					row5.add(0);
					String[] array=outElement.getBindings().get(j).getLocation().split(":", 2);
					boolean lhs_specific_outpattern=false;
					row3.add(Integer.parseInt(array[0]));
					int fr=IfSingleAttribute(row2,outElement,j);
						
					for(int k=NSGAIIThreeStep.classnamestartpointtarget.get(index);k<NSGAIIThreeStep.classnamestartpointtarget.get(index)+NSGAIIThreeStep.classnamelengthtarget.get(index) ;k++) {
						if(outElement.getBindings().get(j).getPropertyName().equals( NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(k).getName())) {
				       
							lhs_specific_outpattern=true;
							row6=AddLHSOfBindingstoList(row6,k);
							row7=AddLowerBoundOfLHSOfBindingstoList(row7,k);
							row8=AddUpperBoundOfLHSOfBindingstoList(row8,k);	
						
							if(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(k).getUpperBound()==1
								&& NSGAIIThreeStep.array_status==false && fr==1)
							{
								NSGAIIThreeStep.listmandatoryfeaturelocation.add( Integer.parseInt(array[0]));
						
							}
					}
				}
					
					if(lhs_specific_outpattern==false) {
						
						for(int i=0;i<NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.size();i++) {
							if(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getName().equals( outElement.getBindings().get(j).getPropertyName())) {
							
								row6=AddLHSOfBindingstoList(row6,i);
								row7=AddLowerBoundOfLHSOfBindingstoList(row7,i);
								row8=AddUpperBoundOfLHSOfBindingstoList(row8,i);	
								AddMandatoryFeatureLocationtoList(i, array);
								
							}
						}
						
					}
				}
				row=row+1;
			  InitializeArrayLists(row2, row6,row7, row8, row3,row5);   
			}
		}
	
		NSGAIIThreeStep.array_status=true;
	}
	private EObject SpecificOutPattern(ATLModel wrapper2, int indexoutpattern) {
		
		List<OutPatternElement> modifiable = (List<OutPatternElement>) wrapper.allObjectsOf(OutPatternElement.class);
		EStructuralFeature featureDefinitionout = wrapper.source(modifiable.get(indexoutpattern)).eClass()
				.getEStructuralFeature("type");
		EObject object2modify_src3 = wrapper.source(modifiable.get(indexoutpattern));
		EObject oldFeatureValue3 = (EObject) object2modify_src3.eGet(featureDefinitionout);
	   return oldFeatureValue3;
	}
	private int IfSingleAttribute(ArrayList<String> row2, OutPatternElement outElement, int j) {
		
		int fr=0;
		for(int f=0;f<row2.size();f++ )
			if( row2.get(f).equals( outElement.getBindings().get(j).getPropertyName() ))
				fr++;
		return fr;
	}
	private ArrayList<Integer> AddUpperBoundOfLHSOfBindingstoList(ArrayList<Integer> row8, int k) {
		
		row8.add(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(k).getUpperBound() );
		return row8;
	}
	private ArrayList<Integer> AddLowerBoundOfLHSOfBindingstoList(ArrayList<Integer> row7, int k) {
		
		row7.add(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(k).getLowerBound() );
		return row7;
	}
	private ArrayList<String> AddLHSOfBindingstoList(ArrayList<String> row6, int k) {
		
		row6.add( NSGAIIThreeStep.listnavigationtypeinheritattrtarget.get(k));
		return row6;
	}
	private void AddMandatoryFeatureLocationtoList(int i, String[] array) {
		
		if(NSGAIIThreeStep.listinheritattributesourcemetamodeltarget.get(i).getUpperBound()==1
				&& NSGAIIThreeStep.array_status==false)
		{
		NSGAIIThreeStep.listmandatoryfeaturelocation.add( Integer.parseInt(array[0]));
		
		}
		
	}
	private void InitializeArrayLists(ArrayList<String> row2, ArrayList<String> row6, ArrayList<Integer> row7,
			ArrayList<Integer> row8, ArrayList<Integer> row3, ArrayList<Integer> row5) {
		
		this.listpropertyname.add(row2);		
		this.listpropertytype.add(row6);
		this.listlowerboundcardinality.add(row7);
		this.listupperboundcardinality.add(row8);
		this.listpropertynamelocation.add(row3);
		this.originalwrapper.add(row5);
		
	}
	private void ClearArrayLists() {
		
		this.listpropertyname.clear();
		this.listpropertytype.clear();
		this.listlowerboundcardinality.clear();
		this.listupperboundcardinality.clear();
		this.listpropertynamelocation.clear();
		this.originalwrapper.clear();
		 listpropertyname = new ArrayList<ArrayList<String>>();
		 listpropertytype = new ArrayList<ArrayList<String>>();
		 listlowerboundcardinality = new ArrayList<ArrayList<Integer>>();
		 listupperboundcardinality = new ArrayList<ArrayList<Integer>>();
		 listpropertynamelocation = new ArrayList<ArrayList<Integer>>();
		 originalwrapper = new ArrayList<ArrayList<Integer>>();
		
		
	}
	protected int FindIndexRule(String[] array) {

		int indexrule = -1;
		for (int j = 0; j < NSGAIIThreeStep.faultrule.size(); j++) {

			if (Integer.parseInt(array[0]) > NSGAIIThreeStep.faultrule.get(j)
					&& Integer.parseInt(array[0]) < NSGAIIThreeStep.finalrule.get(j))

				indexrule = j;
		}
		return indexrule;

	}
	
	private /*static*/ long index = 1;
	private String getValidNameOfFile (String outputFolder, Setting s2) throws IOException {
		
		String outputfile = null;
		String aux        = null;
		File file1 = new File(outputFolder);	
		file1.mkdir();
		File file = new File(outputFolder+"/transformation"+Class2Rel.totalnumber);
		deleteDirectory(outputFolder+"/transformation"+Class2Rel.totalnumber,true);        
		file.mkdir();
		aux = File.separatorChar + "/transformation" +Class2Rel.totalnumber+ "/"+s2.gettrafo()+".atl";
		outputfile = outputFolder + aux;
		return outputfile;
	}
	
	public void save (EMFModel atlModel, String outputFolder, Setting s) throws IOException {
		try {
			// save atl file
			String atl_transformation = this.getValidNameOfFile(outputFolder,s);
			AtlParser atlParser       = new AtlParser();
			atlParser.extract(atlModel, atl_transformation);
			String asm_transformation = atl_transformation.replace(".atl", ".asm");
		} 
		catch (ATLCoreException e) {}		
	}
	

	private void deleteDirectory (String directory, boolean recursive) {
		File folder = new File(directory);		
		if (folder.exists())
			for (File file : folder.listFiles()) {				
				if (file.isDirectory()) deleteDirectory(file.getPath(), recursive);
				
				file.delete();
				
				
			}
		folder.delete();
		
	}
	
	OperationsThreeStep() throws Exception 
    {
		//Here I have to read and convert the ATL rule
		if(NSGAIIThreeStep.postprocessing==false) {
		Setting s=new Setting();
		   Testeroperate(s.getinputfaultytransformation(), s.getoutputfolder());
		}
		else {
			
			Settingpostprocessing s=new Settingpostprocessing();
			   Testeroperate(NSGAII.inputfaultytransformation, NSGAIIpostprocessing.combinedfaultytransformations);
			
		}
		}
	
	public OperationsThreeStep(String atlfilepath, EMFModel atlModel2, ATLModel atlModel4) throws Exception 
    {
		
		Setting s=new Setting();
		
			typing(atlfilepath, new Object[] {s.getsourcemetamodel(),s.gettargetmetamodel() }, 
					   new String[] { s.getsecondecorename(), s.getfirstecorename()}, true,atlModel2,atlModel4);
					
		 analyser = getAnalyser();
		 model=analyser.getATLModel();
    }
	
	public   List<Object> executeOperations(int num,CoSolutionThreeStep S,int sizeoperation, Solution solution, int sumfirstfit, int sumsecondfit, int sumthirdfit, FileWriter csvWriter, CommonFunctionOperators cp, LaunchATLHelper atlLauncher)
			throws Exception{
		
		Setting s=new Setting();	
		if (Class2Rel.checkoffspring==1)
			try {
			Testeroperate(s.getinputfaultytransformation(), s.getoutputfolder());
				Class2Rel.checkoffspring=0;
			} catch (ATLCoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (transException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
				
			List<Object> comments = null;
			
			//if(num==3) {
				try {
			
			switch (num) {
			case 1:
				statecase="case1";
				AbstractMutator[] operators = {
						
					 new OutElementModificationMutatorThreeStep(),
					 
						};
				 comments = null;
				for (AbstractMutator operator : operators) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
			
				 this.wrapper=(ATLModel) comments.get(0);
				this.atlModel=(EMFModel) comments.get(1);
				 break;
				
			case 2:
				statecase="case2";

				AbstractMutator[] operators2 = {
						
					new PredefinedOperationModificationMutatorThreeStep(),
						};
				
				comments = null;
				for (AbstractMutator operator : operators2) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
					}
				
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 3:
				statecase="case10";
				System.out.println("modify3");
				AbstractMutator[] operators3 = {
						new BindingModificationMutatorThreeStep(),
					};
				comments = null;
				for (AbstractMutator operator : operators3) {
	
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
			        
					}
				
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 4:
				statecase="case4";
				System.out.println("modify4");
				AbstractMutator[] operators4 = {
						 new CollectionModificationMutatorThreeStep(),
						};
		        comments = null;
				for (AbstractMutator operator : operators4) {
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
					
					}
			
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
			case 5:
				statecase="case5";
				System.out.println("modify5");
				AbstractMutator[] operators5 = {
						new CollectionOperationModificationMutatorThreeStep(),
					};
				comments = null;
				for (AbstractMutator operator : operators5) {
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
			
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 6:
				
				statecase="case6";
				System.out.println("modify6");
				AbstractMutator[] operators6 = {
						 new InElementModificationMutatorThreeStep(),
			
				};
				comments = null;
				for (AbstractMutator operator : operators6) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
				this.wrapper=(ATLModel) comments.get(0);
				this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 7:
				 
				statecase="case7";
				System.out.println("modify7");
				AbstractMutator[] operators7 = {
					new ArgumentModificationMutatorThreeStep(),
						
				};
				comments = null;
				for (AbstractMutator operator : operators7) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
			case 8:
				 
				statecase="case8";
				System.out.println("modify8");
				AbstractMutator[] operators8 = {
						 new BindingCreationMutatorThreeStep(),			
				};
				comments = null;
				for (AbstractMutator operator : operators8) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				
       case 9:
				 
				statecase="case9";
				System.out.println("modify9");
				AbstractMutator[] operators9 = {
						new NavigationModificationMutatorThreeStep(),
						
				};
				comments = null;
				for (AbstractMutator operator : operators9) {
					
					comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
				
					}
				 this.wrapper=(ATLModel) comments.get(0);
				 this.atlModel=(EMFModel) comments.get(1);
				 break;
				 
       case 10:
			 
			statecase="case3";
			System.out.println("modify10");
			AbstractMutator[] operators10 = {
					 new IteratorModificationMutatorThreeStep(),
					
			};
			comments = null;
			for (AbstractMutator operator : operators10) {
				
				comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
			
				}
			 this.wrapper=(ATLModel) comments.get(0);
			 this.atlModel=(EMFModel) comments.get(1);
			 break;
				
			 
       case 11:
			 
			statecase="case11";
			System.out.println("modify11");
			AbstractMutator[] operators11 = {
					new BindingDeletionMutator(),
					
			};
			comments = null;
			for (AbstractMutator operator : operators11) {
				
				comments=operator.generateMutants(atlModel, this.iMetaModel, this.oMetaModel, this.folderMutants,this.wrapper,solution,cp);
			
				}
			 this.wrapper=(ATLModel) comments.get(0);
			 this.atlModel=(EMFModel) comments.get(1);
			 break;
		
			}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
		try {
			if(sizeoperation==MyProblemThreeStep.indexoperation && NSGAIIThreeStep.numop>0) {
				comments=SaveATLFile(this.atlModel,s.getnewoutputresult(),s,this.wrapper,
						sumfirstfit,sumsecondfit,sumthirdfit,csvWriter, atlLauncher,solution,s.gettrafo(),comments);
			}
			if(NSGAIIThreeStep.numop==0 && sizeoperation==MyProblemThreeStep.indexoperation) {
				
				comments=SaveATLFile(this.atlModel,s.getnewoutputresult(),s,this.wrapper,
						sumfirstfit,sumsecondfit,sumthirdfit,csvWriter, atlLauncher,solution,s.gettrafo(),comments);
					}
		
		} catch (Exception  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	 	
		return comments;
	 }
	
	private List<Object> SaveATLFile(EMFModel atlModel2, String getnewoutputresult, Setting s, ATLModel wrapper2,
			int sumfirstfit, int sumsecondfit, int sumthirdfit, FileWriter csvWriter, LaunchATLHelper atlLauncher,
			Solution solution, String gettrafo, List<Object> comments) {
		
		try {
			this.save(atlModel,
					s.getnewoutputresult()+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity,s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Object> CoSolutionco = null;
		try {
			CoSolutionco = CalculateNumberError(this.atlModel,this.wrapper,sumfirstfit,sumsecondfit,sumthirdfit,csvWriter, atlLauncher,solution,s.gettrafo());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(comments==null) {
			List<Object> comments2 = new ArrayList<Object>();
			comments2.add((int) CoSolutionco.get(2));
			comments2.add((int) CoSolutionco.get(3));
			comments2.add((int) CoSolutionco.get(4));
			return comments2;
		}
		else {
			comments.add((int) CoSolutionco.get(2));
			comments.add((int) CoSolutionco.get(3));
			comments.add((int) CoSolutionco.get(4));
			return comments;
		}	
	}
	private List<Object> CalculateNumberError(EMFModel atlModel2, ATLModel atlModel4, int sumfirstfit, int sumsecondfit, int sumthirdfit, FileWriter csvWriter, LaunchATLHelper atlLauncher, Solution solution, String trafo) 
			throws Exception {
		
		 CoSolutionThreeStep cocou=new CoSolutionThreeStep();
		 List<Object> ReturnResult = new ArrayList<Object>();
		 int fit3=0;
		 int diffmodel = 1000;
		 
		 // test AnAtlyzer
		 /*		 try {
		 			 //Setting setting=new Setting();
		 			 newfile="Tmp/varaminz/step3new/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation" + Class2Rel.totalnumber +"/"+ trafo+".atl";
		 				cocou.setOp(new OperationsThreeStep(newfile,atlModel2,atlModel4));
		 		} catch (Exception e) {
		 			// TODO Auto-generated catch block
		 			e.printStackTrace();
		 		}
		 		 int nberrors=cocou.getOp().getAnalyser().getATLModel().getErrors().getNbErrors();
		 		 System.out.println(nberrors); */
		 	
		 try {
			 
			 Resource[] ret = atlLauncher.applyTransformationmodels(
					 NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber +"/" +"target2" + Class2Rel.totalnumber +".xmi",
					 NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber +"/" +"target3" + Class2Rel.totalnumber +".xmi",
					 NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber +"/" +"target0" + Class2Rel.totalnumber +".xmi",
					 NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber +"/" +"target1" + Class2Rel.totalnumber +".xmi",
					 NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation" + Class2Rel.totalnumber +"/", trafo,NSGAIIThreeStep.model_index
		                );
			 for(int i=0;i<ret.length;i++) {
				 if(ret[i]!=null) {
				 ret[i].save(Collections.EMPTY_MAP);
					// remove resource from its ResourceSet, memory leak otherwise
					ret[i].getResourceSet().getResources().remove(ret[i]);
				 }
			 } 
		 }
		 catch (IllegalArgumentException | NullPointerException | IllegalValueException exception) {
	            System.out.println("IllegalArgumentExceptionppppppp");
	            DeleteIncorrectATLFile();
	            throw exception; 
	        }
			
		 try {
			
		    diffmodel=compare2(solution);
		    FreeMemory();
		 		
		 } 
		 catch (Exception exception) {
	            System.out.println("IllegalArgumentException compare");
	            DeleteIncorrectATLFile();
	            throw exception; 
	        }
		 
		 cocou.setsecondfit(diffmodel);
		 NSGAIIThreeStep.fitness2=diffmodel;
		 ReturnResult.add(diffmodel);
		 NSGAIIThreeStep.fitness3=fit3;
		 cocou.setThirdfit(fit3);
		 ReturnResult.add(fit3);
		 if(NSGAIIThreeStep.counter%this.populationsize==0 ) {
			 
			 ReturnResult=InitializeReturnResultParameters(0,0,0,ReturnResult);
				
		 }
		 else {
			   
			 ReturnResult=InitializeReturnResultParameters(sumfirstfit,sumsecondfit,sumthirdfit,ReturnResult);
			 }
		
	 return ReturnResult;
		
	}
	
	private List<Object> InitializeReturnResultParameters(int j, int i, int k, List<Object> ReturnResult) {
		
		ReturnResult.add( j);
		ReturnResult.add( i);
		ReturnResult.add( k);
		
		return ReturnResult;
	}
	private void FreeMemory() {
	
		if(Class2Rel.totalnumber==10000
				|| Class2Rel.totalnumber==20000
				|| Class2Rel.totalnumber==30000
				|| Class2Rel.totalnumber==40000)
			System.gc();
		
	}
	private void DeleteIncorrectATLFile() {
		
	       NSGAIIThreeStep.numberincorrectatl++;
           // First, remove files from into the folder 
           Path path 
           = Paths.get(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber); 
           File file = new File(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+ Class2Rel.totalnumber);    
           String[] myFiles;    
           if (file.isDirectory()) {
               myFiles = file.list();
               for (int i = 0; i < myFiles.length; i++) {
                   File myFile = new File(file, myFiles[i]); 
                   myFile.delete();
               }
           }
           try {
				Files.delete(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}	
	}
	private ArrayList<String> ReadInputDiff(EList<Diff> differences) {
		
		 
		ArrayList<String> Diffsubset = new ArrayList<String>();
		  
		  String line;
		 
			  for(int i=0;i<differences.size();i++)
				{
				line=differences.get(i).toString();
				if(line.contains("reference")) {
				String[] arr = line.split(",");
				String[] arr2 = arr[0].split("\\{");
				StringBuilder builder = new StringBuilder();
				builder .append(arr2[1].trim() );
				builder .append("-");
				String [] arr3= arr[1].trim().split("@|\\s+");
				builder .append(arr3[0].trim() );
				
				if(arr3.length>=3)
				builder .append(arr3[2].trim() );
				String [] arr4= arr[2].trim().split("left");
				if(arr4.length>1)
				if (!arr4[1].equals("=<null>")) {
					builder .append("-left");
					String [] arr6= arr4[1].trim().split("=|@|\\s+");
					builder .append(arr6[1].trim() );
					if(arr6.length>3)
						builder .append(arr6[3].trim() );
					}
				String [] arr5= arr[3].trim().split("right");
				if(arr5.length>1)
				if (arr5[1]!="=<null>") {
				builder .append("-right");
				String [] arr6= arr5[1].trim().split("=|@|\\s+");
				builder .append(arr6[1].trim() );
				if(arr6.length>3)
					builder .append(arr6[3].trim() );
				}
				String [] arr7=null;
				if(arr.length>7)
				 arr7= arr[7].trim().split("left");
				if(!arr7.equals("null"))
				if(arr7.length>1)
				if (!arr7[1].equals("=<null>")) {
					builder .append("-left");
					String [] arr6= arr7[1].trim().split("=|@|\\s+");
					builder .append(arr6[1].trim() );
					if(arr6.length>3)
						builder .append(arr6[3].trim() );
					}
				String [] arr8=null;
				if(arr.length>8)
				  arr8= arr[8].trim().split("right");	
				if(arr8!=null)
				if (!arr8[1].equals("=<null>")) {
				builder .append("-right");
				String [] arr6= arr8[1].trim().split("=|@|\\s+");
				builder .append(arr6[1].trim() );
				if(arr6.length>3)
					builder .append(arr6[3].trim() );
				}
				Diffsubset.add( builder.toString());
				}
				else {
					String [] arr7= line.trim().split("resourceURI");
					Diffsubset.add(arr7[0]);
					
				}
				
				}
				  
		return Diffsubset;
		  
	  }
	
	public int compare2(Solution solution) throws Exception {
		

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new EcoreResourceFactoryImpl());
		ResourceSet rs = new ResourceSetImpl();
		final ExtendedMetaData extendedMetaData = new BasicExtendedMetaData(rs.getPackageRegistry());
		rs.getLoadOptions().put(XMLResource.OPTION_EXTENDED_META_DATA, extendedMetaData);
		String s2=System.getProperty("java.classpath");
		Setting s=new Setting();
		URI modelURI = URI.createFileURI(s.gettargetmetamodel());
		Resource r = rs.getResource(modelURI, true);
		EObject eObject = r.getContents().get(0);
		if (eObject instanceof EPackage) {
			EPackage p = (EPackage)eObject;
			rs.getPackageRegistry().put(p.getNsURI(), p);
		}
			
		// Register the factory
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
		EMFCompare comparator;
		IComparisonScope scope;
		Comparison comparison;
		EList<Diff> differences;
		int differenceCount=0;
		if(NSGAIIThreeStep.model_index.contains(3)) {
		// Load the two input models
		ResourceSet resourceSet1 = new ResourceSetImpl();
		ResourceSet resourceSet2 = new ResourceSetImpl();
		URI uri1 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+Class2Rel.totalnumber+"/target3"+Class2Rel.totalnumber+".xmi");
	    URI uri2 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/correctoutput3.xmi");
	    resourceSet1.getResource(uri1, true);
		resourceSet2.getResource(uri2, true);    
		// Configure EMF Compare
		comparator = EMFCompare.builder().build();
		// Compare the two models
		scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
		comparison = comparator.compare(scope);
		differences = comparison.getDifferences();
		System.out.println(differences.size());
		//ArrayList<String> Diffsubsetmodel3 =ReadInputDiff(differences) ;
		// solution.Diffsubsetmodel3=CompareDiffsubset(NSGAIIThreeStep.Diffsubset3,Diffsubsetmodel3,solution.Diffsubsetmodel3);	 
		differenceCount = differences.size();
		
		}
		
		if(NSGAIIThreeStep.model_index.contains(2)) {
		ResourceSet resourceSet3 = new ResourceSetImpl();
		ResourceSet resourceSet4 = new ResourceSetImpl();
		URI uri3 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+Class2Rel.totalnumber+"/target2"+Class2Rel.totalnumber+".xmi");
		URI uri4 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/correctoutput2.xmi");
	    try {
		resourceSet3.getResource(uri3, true);
		resourceSet4.getResource(uri4, true);
		// Configure EMF Compare
		comparator = EMFCompare.builder().build();
		// Compare the two models
		scope = new DefaultComparisonScope(resourceSet3, resourceSet4, null);
		comparison = comparator.compare(scope);	
		differences = comparison.getDifferences();
		System.out.println(differences.size());
		//ArrayList<String> Diffsubsetmodel2 =ReadInputDiff(differences) ;
		// solution.Diffsubsetmodel2=CompareDiffsubset(NSGAIIThreeStep.Diffsubset2,Diffsubsetmodel2,solution.Diffsubsetmodel2);	
		differenceCount= differenceCount+differences.size();
	    }
	    catch(Exception e) {
	    	
	     }
	    
		}
		
		if(NSGAIIThreeStep.model_index.contains(0)) {
			ResourceSet resourceSet5 = new ResourceSetImpl();
			ResourceSet resourceSet6 = new ResourceSetImpl();
			URI uri5 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+Class2Rel.totalnumber+"/target0"+Class2Rel.totalnumber+".xmi");
			URI uri6 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/correctoutput0.xmi");
		    resourceSet5.getResource(uri5, true);
			resourceSet6.getResource(uri6, true);
			// Configure EMF Compare
			comparator = EMFCompare.builder().build();
			// Compare the two models
			scope = new DefaultComparisonScope(resourceSet5, resourceSet6, null);
			comparison = comparator.compare(scope);	
			differences = comparison.getDifferences();
			System.out.println(differences.size());
			//ArrayList<String> Diffsubsetmodel0 =ReadInputDiff(differences) ;
		    //solution.Diffsubsetmodel0=CompareDiffsubset(NSGAIIThreeStep.Diffsubset0,Diffsubsetmodel0,solution.Diffsubsetmodel0);
			differenceCount= differenceCount+differences.size();
			}
		if(NSGAIIThreeStep.model_index.contains(1)) {
			ResourceSet resourceSet7 = new ResourceSetImpl();
			ResourceSet resourceSet8 = new ResourceSetImpl();
			URI uri7 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/experiment"+Class2Rel.atlindex+Class2Rel.numberdiversity+"/transformation"+Class2Rel.totalnumber+"/target1"+Class2Rel.totalnumber+".xmi");
			URI uri8 = URI.createFileURI(NSGAIIThreeStep.startingroot+"/varaminz/"+NSGAIIThreeStep.step+"/examples/correctoutput1.xmi");
		    resourceSet7.getResource(uri7, true);
			resourceSet8.getResource(uri8, true);
			// Configure EMF Compare
			comparator = EMFCompare.builder().build();
			// Compare the two models
			scope = new DefaultComparisonScope(resourceSet7, resourceSet8, null);
			comparison = comparator.compare(scope);	
			differences = comparison.getDifferences();
			//ArrayList<String> Diffsubsetmodel1 =ReadInputDiff(differences) ;	
			//solution.Diffsubsetmodel1=CompareDiffsubset(NSGAIIThreeStep.Diffsubset1,Diffsubsetmodel1,solution.Diffsubsetmodel1);
			differenceCount= differenceCount+differences.size();
			}
		System.out.println("difference");
		System.out.println(differenceCount);
		return differenceCount;
		
	}
	
	public void compare() {
		
	    URI uri1 = URI.createFileURI("C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/outputfolder/resultformodeltransformation1/transformation1/target2.xmi");
	    URI uri2 = URI.createFileURI("C:/Users/varaminz/Downloads/Project/workspaceTEST34/anatlyzer.atl.tests.api/outputfolder/resultformodeltransformation1/transformation1/output2.xmi");
	    Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());    
	    ResourceSet resourceSet = new ResourceSetImpl();
		ResourceSet resourceSet1 = new ResourceSetImpl();
		ResourceSet resourceSet2 = new ResourceSetImpl();
		Setting s=new Setting();
		Resource ecoreResource = resourceSet.getResource(URI.createURI(s.gettargetmetamodel()), true);
		EList<EObject> packages = ecoreResource.getContents();
		for (EObject packageObject : packages) {
			EPackage ePackage = (EPackage) packageObject;
			String nsURI = ePackage.getNsURI();
			EPackage.Registry.INSTANCE.put(nsURI,ePackage); 
		}

		resourceSet1.getResource(uri1, true);
		resourceSet2.getResource(uri2, true);
				// Compare the two models
		IComparisonScope scope = new DefaultComparisonScope(resourceSet1, resourceSet2, null);
	}
}