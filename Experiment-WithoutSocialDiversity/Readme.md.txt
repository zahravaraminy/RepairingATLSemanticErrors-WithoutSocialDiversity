For each <Transformation Problem>
    Input_Metamodel.ecore
    Output_Metamodel.ecore
    x-inputmodel0.xmi
    .
    .
    x-inputmodel3.xmi
    Expectedoutputfor_x-inputmodel0.xmi
    .
    .
    Expectedoutputfor_x-inputmodel3.xmi
    Input2Output_transformation.atl
        For each x-Mutants
	    For each combinedfaultytransformation  
            	For each Run_i
                	patchedtransformation.atl
                	producedmodel_withpatchedtransformationfor_inputmodel0.xmi
			.
			.
			producedmodel__withpatchedtransformationfor_inputmodel3.xmi
		combinedfaultytransformation(CFT).atl
		Diff_producedmodelwith CFT forInputmodel0 withExpectedmodel0
		.
		.
		Diff_producedmodelwith CFT forInputmodel3 withExpectedmodel3
                
    