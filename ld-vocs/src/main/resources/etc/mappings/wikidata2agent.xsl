<?xml version="1.0" encoding="UTF-8"?>

<!--
-->

<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:edm="http://www.europeana.eu/schemas/edm/"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdaGr2="http://rdvocab.info/ElementsGr2/"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:wkde="http://www.wikidata.org/entity/"
    xmlns:schema="http://schema.org/"
    exclude-result-prefixes="rdfs dcterms foaf wkde schema">

    <xsl:output indent="yes" encoding="UTF-8"/>

    <xsl:param name="rdf_about">
        <xsl:text></xsl:text>
    </xsl:param>

    <xsl:template match="/">
                <xsl:apply-templates select="rdf:RDF/rdf:Description"/>
        <!-- 
        <xsl:choose>
            <xsl:when test="$rdf_about=''">
                <xsl:apply-templates select="rdf:RDF/rdf:Description[@rdf:about=$rdf_about]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="rdf:RDF/rdf:Description"/>
            </xsl:otherwise>
        </xsl:choose>
         -->
    </xsl:template>

    <xsl:template match="/rdf:RDF/rdf:Description">

        <edm:Agent>

            <xsl:copy-of select="@rdf:about"/>

            <xsl:for-each select="rdfs:label">
                <xsl:element name="skos:prefLabel">
                    <xsl:copy-of select="@xml:lang"/>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="skos:altLabel">
                <xsl:element name="skos:altLabel">
                    <xsl:copy-of select="@xml:lang"/>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="entity:P27">
                <xsl:element name="skos:note">
                    <xsl:copy-of select="@xml:lang"/>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="entity:P646-freebase
                                | entity:P650c
                                | entity:P214-viaf
                                | entity:P227-gnd
                                | entity:P244-lcnaf
                                | entity:P245c
                                | entity:P268c
                                ">
                <xsl:element name="dc:identifier">
                    <xsl:copy-of select="@rdf:resource"/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="entity:P361c">
                <xsl:element name="dcterms:isPartOf">
                    <xsl:copy-of select="@rdf:resource"/>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="schema:description">
                <xsl:element name="rdaGr2:biographicalInformation">
                    <xsl:copy-of select="@xml:lang"/>
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>


            rdaGr2:professionOrOccupation

            <xsl:for-each select="entity:P7 | entity:P9 | entity:P22 | entity:P25 
                                | entity:P26 | entity:P40 | entity:P53 | entity:P156">
                <xsl:element name="edm:isRelatedTo">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:for-each>

        </edm:Agent>

    </xsl:template>

</xsl:stylesheet>