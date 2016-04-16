/*
 * Copyright 2015-2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.nebula.lint

import org.codehaus.groovy.ast.ASTNode
import org.codenarc.rule.Rule
import org.codenarc.rule.Violation

class GradleViolation extends Violation {
    Level level
    File buildFile
    List<GradleLintFix> fixes = []

    public GradleViolation(Level level, File buildFile, Rule rule, Integer lineNumber,
                           String sourceLine, String message) {
        this.level = level
        this.buildFile = buildFile
        this.rule = rule
        this.lineNumber = lineNumber
        this.sourceLine = sourceLine
        this.message = message
    }

    boolean isFixable() { !fixes.isEmpty() }

    Level getLevel() {
        this.level ?: rule.defaultLevel
    }

    static enum Level {
        Info(0), Trivial(1), Warning(2), Error(3)

        int priority
        Level(int priority) { this.priority = priority }
    }

    GradleViolation insertAfter(ASTNode node, String changes) {
        fixes += new GradleLintInsertAfter(buildFile, node.lastLineNumber, changes)
        this
    }

    GradleViolation insertBefore(ASTNode node, String changes) {
        fixes += new GradleLintInsertBefore(buildFile, node.lineNumber, changes)
        this
    }

    GradleViolation replaceWith(ASTNode node, String changes) {
        fixes += new GradleLintReplaceWith(buildFile, node.lineNumber..node.lastLineNumber, node.columnNumber,
            node.lastColumnNumber, changes)
        this
    }

    GradleViolation delete(ASTNode node) {
        fixes += new GradleLintReplaceWith(buildFile, node.lineNumber..node.lastLineNumber, node.columnNumber, node.lastColumnNumber, '')
//        fixes += new GradleLintDelete(buildFile, node.lineNumber..node.lastLineNumber)
        this
    }
}