/*
 * SonarLint for Eclipse
 * Copyright (C) 2015-2023 SonarSource SA
 * sonarlint@sonarsource.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonarlint.eclipse.ui.internal.job;

import java.util.stream.Collectors;
import org.sonarlint.eclipse.core.internal.SonarLintCorePlugin;
import org.sonarlint.eclipse.core.internal.engine.connected.ConnectedEngineFacade;
import org.sonarlint.eclipse.core.internal.jobs.TaintIssuesUpdateAfterSyncJob;
import org.sonarlint.eclipse.ui.internal.util.PlatformUtils;

public class TaintIssuesJobsScheduler {
  private TaintIssuesJobsScheduler() {
    // utility class
  }
  
  /**
   *  After the preference regarding the new code period is changed, we have to update the markers in the
   *  SonarLint Taint Vulnerabilites view as it is not done like the other issues via a new analysis!
   */
  public static void scheduleUpdateAfterNewCodePeriodChange() {
    var openedFiles = PlatformUtils.collectOpenedFiles(null, f -> true);
    openedFiles.keySet().forEach(project -> {
      var bindingOpt = SonarLintCorePlugin.getServersManager().resolveBinding(project);
      if (bindingOpt.isPresent()) {
        var facade = (ConnectedEngineFacade)bindingOpt.get().getEngineFacade();
        var files = openedFiles.get(project).stream()
          .map(file -> file.getFile())
          .collect(Collectors.toList());
        
        new TaintIssuesUpdateAfterSyncJob(facade, project, files).schedule();
      }
    });
  }
}
