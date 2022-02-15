package modification.feature.operator;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.m2m.atl.core.emf.EMFModel;
import witness.generator.MetaModel;
import anatlyzer.atl.model.ATLModel;
import anatlyzer.atlext.ATL.Binding;
import cd.udem.fixingatlerror.Class2Rel;
import cd.udem.fixingatlerror.CommonFunctionOperators;
import cd.udem.fixingatlerror.Setting;
import jmetal.core.Solution;
import transML.exceptions.transException;
public class BindingModificationMutator extends AbstractFeatureModificationMutator {

	@Override
	public List<Object> generateMutants(EMFModel atlModel, MetaModel inputMM, MetaModel outputMM, String outputFolder,ATLModel wrapper,Solution solution,CommonFunctionOperators cp) {
		Class2Rel.typeoperation="binding";
		List<Object> comments = null;
		comments=this.genericAttributeModification(atlModel, outputFolder, Binding.class, "propertyName", outputMM,wrapper,solution);
		Class2Rel.typeoperation=null;
		
		return comments;
	}

	@Override
	public String getDescription() {
		return "Binding-target Modification";
	}

	@Override
	protected List<Object> replacements(EMFModel atlModel,EClass oldFeatureValue,EObject object2modify, String currentAttributeValue, MetaModel metamodel) {
		 return  this.featureReplacements( oldFeatureValue);   
	}

	private List<Object> featureReplacements(EClass inputclassifier) {
		 // TODO Auto-generated method stub
		 Setting s=new Setting();
		 List<Object> replacements = new ArrayList<Object>();
		 String MMRootPath3     = s.getsourcemetamodel();
		 MetaModel meta=null;
		 try {
			meta=new MetaModel(MMRootPath3);
		} catch (transException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		 List<Object> mainclass = new ArrayList<Object>();	 
		 List<EStructuralFeature> mainclass4 = new ArrayList<EStructuralFeature>();
		 	for (EClassifier classifier : meta.getEClassifiers()) {
				if (classifier instanceof EClass) {
						
					for (int  y=0;y<classifier.eContents().size();y++)
					{
						
						if (classifier.eContents().get(y) instanceof EAttribute
								||classifier.eContents().get(y) instanceof EReference )
							mainclass4.add( (EStructuralFeature) classifier.eContents().get(y));
					}
					
				}
			}
		for (EStructuralFeature o : mainclass4) {
			if (o!=null) {
				mainclass.add(o.getName());
			}
		}

		return mainclass;
	}
}
