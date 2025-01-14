package jal.voca.lang

import java.util.function.Supplier

data class Word(
    val word: String, 
    val typeInfo: TypeInfo,
) {
    val wordType: WordType?
        get() = typeInfo.type

    val gender: Gender?
        get() = typeInfo.gender

    val cardinality: Cardinality?
        get() = typeInfo.cardinality
    
    val pluralForm: String?
        get() = typeInfo.pluralForm
}

data class TypeInfo(
    val type: WordType? = null, 
    val gender: Gender? = null, 
    val cardinality: Cardinality? = null, 
    val pluralForm: String? = null,
) {
    companion object Parser {
        fun parseTypeInfo(typeInfoS: String): TypeInfo? {
            if (typeInfoS.isEmpty()) {
                return null;
            }
            // n(m)  -> noun, masculine
            val parametersIndex = typeInfoS.indexOf("(")
            if (parametersIndex == -1) {
                return TypeInfo(type = WordType.from(typeInfoS))
            } else {
                val typeS = typeInfoS.substring(0, parametersIndex).trim()
                val type = WordType.from(typeS)
                val parametersEnd = typeInfoS.indexOf(")", parametersIndex)
                val parametersS = typeInfoS.substring(parametersIndex + 1, parametersEnd)
                val parameters = parametersS.split(",")

                if (type == WordType.NOUN) {
                    return getNounTypeInfo(parameters)
                } else {
                    if (parameters.size > 0) {
                        throw RuntimeException("Unknown parameters to word-type: " + typeInfoS)
                    }
                    return TypeInfo(type = type)
                }
            }
        }

        private fun getNounTypeInfo(parameters: List<String>): TypeInfo {
            val g: Gender?
            val c: Cardinality?
            val p: String?
            if (parameters.size >= 1) {
                val genderS = parameters[0].trim()
                g = if (genderS.isEmpty()) null else Gender.from(genderS)
                if (parameters.size >= 2) {
                    val cardinalityS = parameters[1].trim()
                    c = if (cardinalityS.isEmpty()) null else Cardinality.from(cardinalityS)
                    if (parameters.size >= 3) {
                        val pluralForm = parameters[2].trim()
                        if (pluralForm.isEmpty()) {
                            p = null
                        } else {
                            if (pluralForm.startsWith("-")) {
                                p = pluralForm.substring(1)
                            } else {
                                p = pluralForm
                            }
                        }                            
                    } else {
                        p = null
                    }
                } else {
                    c = null
                    p = null
                }
            } else {
                g = null
                c = null
                p = null
            }

            return TypeInfo(WordType.NOUN, g, c, p)
        }
    }

    override fun toString(): String {
        return when(type) {
            null -> ""
            WordType.NOUN -> "n(" +  
                    (if (gender == null) "" else gender.representation) + "," +
                    (if (cardinality == null) "" else cardinality.representation) + "," +
                    (if (pluralForm == null) "" else pluralForm) + ")"
            else -> type.representation
        }
    }
}

interface Representable {
    val representation: String

    companion object {
        fun <E : Representable> fromRepresentation(all: Supplier<Array<E>>, s: String): E {
            for (e in all.get()) {
                if (e.representation == s) {
                    return e;
                }
            }
            throw RuntimeException("Unknown " + s)
        }
    }
}

enum class WordType(override val representation: String) : Representable {
    VERB("v"),
    ADJECTIVE("a"),
    NOUN("n");

    companion object {
        fun from(s: String): WordType = Representable.fromRepresentation(WordType::values, s)
    }
}

enum class Gender(override val representation: String) : Representable {
    MASCULINE("m"),
    FEMININE("f"),
    NEUTER("n");

    companion object {
        fun from(s: String): Gender = Representable.fromRepresentation(Gender::values, s)
    }
}

enum class Cardinality(override val representation: String) : Representable {
    SINGULAR("s"),
    PLURAL("pl");

    companion object {
        fun from(s: String): Cardinality = Representable.fromRepresentation(Cardinality::values, s)
    }
}
