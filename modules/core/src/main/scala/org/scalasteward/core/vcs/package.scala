/*
 * Copyright 2018-2019 Scala Steward contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.scalasteward.core

import org.scalasteward.core.application.SupportedVCS
import org.scalasteward.core.application.SupportedVCS.{Bitbucket, GitHub, Gitlab}
import org.scalasteward.core.data.Update
import org.scalasteward.core.vcs.data.Repo

package object vcs {

  /** Determines the `head` (GitHub) / `source_branch` (GitLab, Bitbucket) parameter for searching
    * for already existing pull requests.
    */
  def listingBranch(vcsType: SupportedVCS, fork: Repo, update: Update): String =
    vcsType match {
      case GitHub =>
        s"${fork.show}:${git.branchFor(update).name}"

      case Gitlab | Bitbucket =>
        git.branchFor(update).name
    }

  /** Determines the `head` (GitHub) / `source_branch` (GitLab, Bitbucket) parameter for creating
    * a new pull requests.
    */
  def createBranch(vcsType: SupportedVCS, fork: Repo, update: Update): String =
    vcsType match {
      case GitHub =>
        s"${fork.owner}:${git.branchFor(update).name}"

      case Gitlab | Bitbucket =>
        git.branchFor(update).name
    }

  def createCompareUrl(repoUrl: String, update: Update): Option[String] = {
    val from = update.currentVersion
    val to = update.nextVersion
    val canonicalized = repoUrl.replaceAll("/$", "")
    if (repoUrl.startsWith("https://github.com/") || repoUrl.startsWith("https://gitlab.com/"))
      Some(s"${canonicalized}/compare/v${from}...v${to}")
    else if (repoUrl.startsWith("https://bitbucket.org/"))
      Some(s"${canonicalized}/compare/${to}..${from}#diff")
    else
      // unsupported VCS or just homepage
      None
  }
}
