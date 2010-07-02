/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package codekeeper;

import codekeeper.Constants.SnippetTypes;
import java.util.Date;

/**
 *
 * @author tattooedpierre
 */
public class SnippetObject {

    public String SnippetName;
    public Constants.SnippetTypes SnippetType;
    public Date DateCreated;
    public String ParentFolder;
    public String RootFolder;
    public String Data;

    public SnippetObject(String snippetName, Constants.SnippetTypes snippetType, Date dateCreated, String data)
    {
        SnippetName = snippetName;
        SnippetType = snippetType;
        DateCreated = dateCreated;
        Data = data;
        
    }

    public String getData()
    {
        return Data;
    }

    public void setData(String Data)
    {
        this.Data = Data;
    }

    public Date getDateCreated()
    {
        return DateCreated;
    }

    public void setDateCreated(Date DateCreated)
    {
        this.DateCreated = DateCreated;
    }

    public String getParentFolder()
    {
        return ParentFolder;
    }

    public void setParentFolder(String ParentFolder)
    {
        this.ParentFolder = ParentFolder;
    }

    public String getRootFolder()
    {
        return RootFolder;
    }

    public void setRootFolder(String RootFolder)
    {
        this.RootFolder = RootFolder;
    }

    public String getSnippetName()
    {
        return SnippetName;
    }

    public void setSnippetName(String SnippetName)
    {
        this.SnippetName = SnippetName;
    }

    public SnippetTypes getSnippetType()
    {
        return SnippetType;
    }

    public void setSnippetType(SnippetTypes SnippetType)
    {
        this.SnippetType = SnippetType;
    }

    public SnippetObject()
    {
        
    }

    @Override
    public String toString()
    {
        return SnippetName;
    }
}
