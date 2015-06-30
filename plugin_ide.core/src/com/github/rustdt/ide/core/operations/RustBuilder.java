/*******************************************************************************
 * Copyright (c) 2015, 2015 Bruno Medeiros and other Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Bruno Medeiros - initial API and implementation
 *******************************************************************************/
package com.github.rustdt.ide.core.operations;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import com.github.rustdt.tooling.RustBuildOutputParser;
import com.github.rustdt.tooling.ops.RustSDKLocationValidator;

import melnorme.lang.ide.core.LangCore;
import melnorme.lang.ide.core.operations.BuildTarget;
import melnorme.lang.ide.core.operations.IBuildTargetOperation;
import melnorme.lang.ide.core.operations.LangBuildManagerProjectBuilder;
import melnorme.lang.ide.core.operations.LangProjectBuilder;
import melnorme.lang.ide.core.utils.ResourceUtils;
import melnorme.lang.tooling.data.PathValidator;
import melnorme.lang.tooling.ops.ToolSourceMessage;
import melnorme.utilbox.core.CommonException;
import melnorme.utilbox.process.ExternalProcessHelper.ExternalProcessResult;

/**
 * Rust builder, using Cargo.
 */
public class RustBuilder extends LangBuildManagerProjectBuilder {
	
	public RustBuilder() {
	}
	
	@Override
	protected PathValidator getBuildToolPathValidator() {
		return new RustSDKLocationValidator();
	}
	
	@Override
	protected ProcessBuilder createCleanPB() throws CoreException, CommonException {
		return createSDKProcessBuilder("clean");
	}
	
	/* ----------------- Build ----------------- */
	
	@Override
	protected IBuildTargetOperation newBuildOperation(IProject project, LangProjectBuilder projectBuilder,
			BuildTarget buildConfig) {
		return new RustRunBuildOperationExtension();
	}
	
	protected class RustRunBuildOperationExtension extends AbstractRunBuildOperation {
		@Override
		protected ProcessBuilder createBuildPB() throws CoreException, CommonException {
			return createSDKProcessBuilder("build");
		}
		
		@Override
		protected void doBuild_processBuildResult(ExternalProcessResult buildAllResult) 
				throws CoreException, CommonException {
			ArrayList<ToolSourceMessage> buildMessage = new RustBuildOutputParser() {
				@Override
				protected void handleMessageParseError(CommonException ce) {
					 LangCore.logStatus(LangCore.createCoreException(ce));
				}
			}.parseOutput(buildAllResult);
			
			addErrorMarkers(buildMessage, ResourceUtils.getProjectLocation(getProject()));
		}
	}
	
}