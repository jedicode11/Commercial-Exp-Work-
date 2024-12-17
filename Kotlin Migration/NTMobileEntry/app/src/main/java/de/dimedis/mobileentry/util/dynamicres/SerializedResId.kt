package de.dimedis.mobileentry.util.dynamicres

/**
 * Created by Softeq Development Corporation
 * http://www.softeq.com
 */
//
//@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class SerializedResId(
    /**
     * @return the desired name of the field when it is serialized
     */
    val value: Int
)