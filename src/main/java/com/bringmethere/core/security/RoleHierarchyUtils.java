package com.bringmethere.core.security;

public class RoleHierarchyUtils {

    public static String getRoleHierarchyStringRepresentation(String... roles) {
        String roleHierarchyStringRepresentation = "";

        for (int i = 0; i < roles.length - 1; i++) {
            if (roleHierarchyStringRepresentation != "") {
                roleHierarchyStringRepresentation += " and ";
            }

            roleHierarchyStringRepresentation += roles[i] + " > " + roles[i + 1];
        }

        return roleHierarchyStringRepresentation;
    }
}