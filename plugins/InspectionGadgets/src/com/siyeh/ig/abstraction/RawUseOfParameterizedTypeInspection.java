package com.siyeh.ig.abstraction;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.psi.*;
import com.siyeh.ig.BaseInspection;
import com.siyeh.ig.BaseInspectionVisitor;
import com.siyeh.ig.GroupNames;
import com.siyeh.ig.VariableInspection;

public class RawUseOfParameterizedTypeInspection extends VariableInspection {

    public String getDisplayName() {
        return "Raw use of parameterized class";
    }

    public String getGroupDisplayName() {
        return GroupNames.ABSTRACTION_GROUP_NAME;
    }

    public String buildErrorString(PsiElement location) {
        return "Raw use of parameterized class #ref #loc";
    }

    public BaseInspectionVisitor createVisitor(InspectionManager inspectionManager, boolean onTheFly) {
        return new RawUseOfParameterizedTypeVisitor(this, inspectionManager, onTheFly);
    }

    private static class RawUseOfParameterizedTypeVisitor extends BaseInspectionVisitor {
        private RawUseOfParameterizedTypeVisitor(BaseInspection inspection, InspectionManager inspectionManager, boolean isOnTheFly) {
            super(inspection, inspectionManager, isOnTheFly);
        }

        public void visitVariable(PsiVariable variable) {
            super.visitVariable(variable);
            final PsiTypeElement typeElement = variable.getTypeElement();
            checkTypeElement(typeElement);
        }

        public void visitTypeCastExpression(PsiTypeCastExpression cast) {
            super.visitTypeCastExpression(cast);
            final PsiTypeElement typeElement = cast.getCastType();
            checkTypeElement(typeElement);
        }

        public void visitInstanceOfExpression(PsiInstanceOfExpression expression){
            super.visitInstanceOfExpression(expression);
            final PsiTypeElement typeElement = expression.getCheckType();
            checkTypeElement(typeElement);
        }

        public void visitNewExpression(PsiNewExpression newExpression) {
            super.visitNewExpression(newExpression);
            final PsiJavaCodeReferenceElement classReference =
                    newExpression.getClassReference();

            if (classReference == null) {
                return;
            }
            final PsiElement referent = classReference.resolve();
            if (referent == null) {
                return;
            }
            if (!(referent instanceof PsiClass)) {
                return;
            }

            final PsiClass referredClass = (PsiClass) referent;
            if (!referredClass.hasTypeParameters()) {
                return;
            }
            if (newExpression.getTypeArgumentList() != null) {
                return;
            }
            registerError(classReference);
        }

        private void checkTypeElement(PsiTypeElement typeElement) {
            if (typeElement == null) {
                return;
            }
            final PsiType type = typeElement.getType();
            if (type == null) {
                return;
            }
            final PsiType componentType = type.getDeepComponentType();
            if (componentType == null) {
                return;
            }
            if (!(componentType instanceof PsiClassType)) {
                return;
            }
            final String typeText = componentType.getCanonicalText();
            if (typeText.indexOf((int) '<') >= 0) {
                return;
            }
            final PsiClass aClass = ((PsiClassType) componentType).resolve();

            if (aClass == null) {
                return;
            }
            if (!aClass.hasTypeParameters()) {
                return;
            }
            final PsiElement typeNameElement =
                    typeElement.getInnermostComponentReferenceElement();
            registerError(typeNameElement);
        }
    }

}
