/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codekeeper;

import codekeeper.Constants.SnippetTypes;

/**
 *
 * @author tattooedpierre
 */
public class SnippetCategory implements Comparable<SnippetCategory>{

    public String SnippetCategoryName;
    public Constants.SnippetTypes SnippetCategoryType;

    public SnippetCategory(String name, Constants.SnippetTypes snippetType)
    {
        SnippetCategoryName = name;
        SnippetCategoryType = snippetType;

    }

    public String getSnippetCategoryName()
    {
        return SnippetCategoryName;
    }

    public void setSnippetCategoryName(String SnippetCategoryName)
    {
        this.SnippetCategoryName = SnippetCategoryName;
    }

    public SnippetTypes getSnippetCategoryType()
    {
        return SnippetCategoryType;
    }

    public void setSnippetCategoryType(SnippetTypes SnippetCategoryType)
    {
        this.SnippetCategoryType = SnippetCategoryType;
    }

    public SnippetCategory()
    {
    }

    @Override
    public String toString()
    {
        return SnippetCategoryName;
    }

    @Override
    public int compareTo(SnippetCategory o)
    {
        return this.SnippetCategoryName.compareTo(o.SnippetCategoryName);
    }

}
