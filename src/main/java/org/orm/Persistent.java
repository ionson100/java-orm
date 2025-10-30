

package org.orm;

/**
 *If a DTO class inherits this class, the ORM can record where the object was obtained from, whether it was created on the client or retrieved from the database, and you can use the save method.
 * You can always know where the object was obtained from.
 *
 * <pre>
 * {@code
 * @MapTable
 * public class MyTable  extends Persistent  {
 *     @MapPrimaryKey
 *     public int id;
 *
 *     @MapColumn
 *     public string name;
 * }
 * }
 * </pre>
 */
public class Persistent {

    /**
     * Default constructor
     */
    public Persistent(){}
    /**
     * true - an object obtained from the database
     * false - the object was created on the client and was not saved in the database
     */
    boolean isPersistent;

    /**
     * Where the object was obtained from, from the database or a new one created
     * true - an object obtained from the database
     * false - the object was created on the client and was not saved in the database
     * @return true from database, false new object
     */
    public boolean isPersistent(){
        return isPersistent;
    }
}
