# Elasticsearch-Phone

Indexing phone numbers & sip addresses in lucene is complicated. Most people use ngram tokenizers. We did that for a while with ngram min=3 & max=35, but the result was often 100s of tokens per sip address. Working in a call center focused company we quickly figured out how wasteful that is on the storage front. For us 6/7ths of our indexes were waisted on useless sip address tokens.

It's a hard problem to regex your way out of. An international phone number often includes a country code, but that can be 1, 2, or 3+ digits. A lot of people have requested elasticsearch integrate google's libphone library into a custom lucene analyzer. It hasn't happened yet, so here's a plugin that attempts to do just that.  

Note: This is a young project we're just starting to do testing on 8/3/2015. We'll improve as time goes on, but use at your own risk.  

# Building and installing the plugin
mvn package  
./bin/plugin --url file:///....elasticsearch-phone/target/releases/elasticsearch-phone-1.0.0.zip --install elasticsearch-phone;

## Example inputs

Provide a telephone or sip address prefixed by "tel:" or "sip:" with no spaces or symbols.

Your indexing template will need to specify the analyzer for the field. EG
            "dnis": {
              "type": "string",
              "analyzer": "phone"
            },


Sample allowed inputs (see PhoneIntegrationTest for more) 
tel:+441344840400   
tel:+498362930830  
sip:abc@autosbcpc  
sip:+13119310462;ext=2244@178.12.10.115:8060  

## Example tokenization

INPUT (with country code derived with google liphone)  

sip:+13169410766;ext=2233@172.17.10.117:8060  

TOKENS  

+13169410766;ext=2233  
1  
2233  
3169410766  
3  
13  
31  
131  
316  
1316  
3169  
13169  
31694  
131694  
316941  
1316941  
3169410  
13169410  
31694107  
131694107  
316941076  
1316941076  
3169410766  
13169410766  

INPUT (without a country code)  

tel:8177148350  

TOKENS  

8177148350  
8  
81  
817  
8177  
81771  
817714  
8177148  
81771483  
817714835  
8177148350  
  