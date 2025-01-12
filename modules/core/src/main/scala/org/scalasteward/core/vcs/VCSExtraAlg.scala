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

package org.scalasteward.core.vcs

import cats.Monad
import cats.implicits._
import org.scalasteward.core.data.Update
import org.scalasteward.core.util.HttpExistenceClient
import org.scalasteward.core.vcs

trait VCSExtraAlg[F[_]] {
  def getBranchCompareUrl(maybeRepoUrl: Option[String], update: Update): F[Option[String]]
}

object VCSExtraAlg {
  def create[F[_]](
      implicit
      existenceClient: HttpExistenceClient[F],
      F: Monad[F]
  ): VCSExtraAlg[F] = new VCSExtraAlg[F] {
    override def getBranchCompareUrl(
        maybeRepoUrl: Option[String],
        update: Update
    ): F[Option[String]] =
      maybeRepoUrl
        .flatMap(vcs.createCompareUrl(_, update))
        .fold(F.pure(Option.empty[String])) { url =>
          existenceClient.exists(url).map {
            case true  => Some(url)
            case false => None
          }
        }
  }
}
