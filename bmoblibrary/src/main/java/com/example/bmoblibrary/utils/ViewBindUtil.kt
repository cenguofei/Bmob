package com.example.bmoblibrary.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.bmoblibrary.ext.logI
import java.lang.Error
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType


@JvmName("inflateVmVbBinding")
fun <VB : ViewBinding> AppCompatActivity.inflateVmVbBinding(layoutInflater: LayoutInflater): VB =
    genericVmVbBindingClass(this) { cls ->
        @Suppress("UNCHECKED_CAST")
        cls.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }.also { binding ->
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = this
        }
    }

@Suppress("UNCHECKED_CAST")
@JvmName("inflateVmVbBinding")
fun <VB : ViewBinding> Fragment.inflateVmVbBinding(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
): VB =
    genericVmVbBindingClass(this) { cls ->
        cls.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
            /**
             * 如果基础方法是静态的，则忽略指定的obj参数。它可以是空的。
             *      FragmentBlankBinding.inflate(layoutInflater,parent,attachToParent)
             *      可以看出inflate方法是静态的
             * 如果基础方法所需的形式参数数量为0，则提供的args数组的长度可能为0或null。
             * 如果基础方法是实例方法，则使用动态方法查找调用它
             * 如果基础方法是静态的，则忽略指定的obj参数。它可以是空的。
             *      FragmentBlankBinding.inflate(layoutInflater,parent,attachToParent)
             *      可以看出inflate方法是静态的
             * 如果基础方法所需的形式参数数量为0，则提供的args数组的长度可能为0或null。
             * 如果基础方法是实例方法，则使用动态方法查找调用它
             * 如果基础方法是静态的，则忽略指定的obj参数。它可以是空的。
             *      FragmentBlankBinding.inflate(layoutInflater,parent,attachToParent)
             *      可以看出inflate方法是静态的
             * 如果基础方法所需的形式参数数量为0，则提供的args数组的长度可能为0或null。
             * 如果基础方法是实例方法，则使用动态方法查找调用它
             * 如果基础方法是静态的，则忽略指定的obj参数。它可以是空的。
             *      FragmentBlankBinding.inflate(layoutInflater,parent,attachToParent)
             *      可以看出inflate方法是静态的
             * 如果基础方法所需的形式参数数量为0，则提供的args数组的长度可能为0或null。
             * 如果基础方法是实例方法，则使用动态方法查找调用它
             */
            .invoke(null, layoutInflater, parent, attachToParent) as VB
    }.also { binding ->
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = viewLifecycleOwner
        }
    }


@JvmName("inflateVbBinding")
fun <VB : ViewBinding> AppCompatActivity.inflateVbBinding(layoutInflater: LayoutInflater): VB =
    genericVbBindingClass(this) { cls ->
        @Suppress("UNCHECKED_CAST")
        cls.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB
    }.also { binding ->
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = this
        }
    }

@Suppress("UNCHECKED_CAST")
@JvmName("inflateVbBinding")
fun <VB : ViewBinding> Fragment.inflateVbBinding(
    layoutInflater: LayoutInflater,
    parent: ViewGroup?,
    attachToParent: Boolean
): VB =
    genericVbBindingClass(this) { cls ->
        cls.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )
            .invoke(null, layoutInflater, parent, attachToParent) as VB
    }.also { binding ->
        if (binding is ViewDataBinding) {
            binding.lifecycleOwner = viewLifecycleOwner
        }
    }


private fun <VB : ViewBinding> genericVbBindingClass(any: Any, block: (Class<VB>) -> VB): VB {
    var genericSuperclass = any.javaClass.genericSuperclass
    var superclass = any.javaClass.superclass
    while (superclass != null) {
        "genericSuperclass:${genericSuperclass?.javaClass?.name}".logI()
        "superclass:${superclass.javaClass.name}".logI()

        if (genericSuperclass is ParameterizedType) {
            try {
                @Suppress("UNCHECKED_CAST")
                return block.invoke(genericSuperclass.actualTypeArguments[0] as Class<VB>)
            } catch (e: NoSuchMethodException) {
            } catch (e: ClassCastException) {
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
        genericSuperclass = superclass.genericSuperclass
        superclass = superclass.superclass
    }
    throw Error("No such ViewBinding Class.")
}

private fun <VB : ViewBinding> genericVmVbBindingClass(any: Any, block: (Class<VB>) -> VB): VB {
    var genericSuperclass = any.javaClass.genericSuperclass
    var superclass = any.javaClass.superclass
    while (superclass != null) {
        "genericSuperclass:${genericSuperclass?.javaClass?.name}".logI()
        "superclass:${superclass.javaClass.name}".logI()

        if (genericSuperclass is ParameterizedType) {
            try {
                @Suppress("UNCHECKED_CAST")
                return block.invoke(genericSuperclass.actualTypeArguments[1] as Class<VB>)
            } catch (e: NoSuchMethodException) {
            } catch (e: ClassCastException) {
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }
        genericSuperclass = superclass.genericSuperclass
        superclass = superclass.superclass
    }
    throw Error("No such ViewBinding Class.")
}