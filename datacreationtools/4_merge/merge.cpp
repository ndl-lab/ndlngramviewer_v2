#include <stdio.h>
#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <deque>
#include "json.hpp"
using namespace std;
using json = nlohmann::json;
vector<string> split(string str, string separator) {
    if (separator == "") return {str};
    vector<string> result;
    string tstr = str + separator;
    long l = tstr.length(), sl = separator.length();
    string::size_type pos = 0, prev = 0;

    for (;pos < l && (pos = tstr.find(separator, pos)) != string::npos; prev = (pos += sl)) {
        result.emplace_back(tstr, prev, pos - prev);
    }
    return result;
}

class Ngramobj{
    public:
    	string materialtype,jsonstr;
    	long count;
    	Ngramobj(string mtype0,long count0,string jsonstr0){
	    materialtype=mtype0;
	    count=count0;
	    jsonstr=jsonstr0;
	}
};


string outputjson(string keyword,vector<Ngramobj>vecobj){
    json j;
    j["keyword"]=keyword;
    for(auto obj :vecobj){
	    j["keywordattribute"].push_back({{"materialtype",obj.materialtype},{"count",obj.count},{"ngramyearjson",obj.jsonstr}});
    }
    return j.dump();
}

int main()
{
    setlocale(LC_ALL,"C.UTF-8");
    ifstream ifsall("sorted-3all.json");
    ofstream ofs("sorted-merge-ngram.json");
    if (ifsall.fail()) {
        cerr << "Failed to open file." << endl;
        return -1;
    }
    bool allflag=true;
    string keyword="";
    vector<Ngramobj>vecobj;
    while(allflag){
	string strall;
	if(!ifsall.eof())getline(ifsall,strall);
	else allflag=false;
	if(allflag){
	    vector<string> res=split(strall,"\t");
	    string resstr=res[2];
	    if(res[0]!=keyword){
		ofs<<outputjson(keyword,vecobj)<<endl;
		vecobj.clear();
	    }
	    if(res[3]=="tosho-pdm")replace(resstr.begin(),resstr.end(),'\'','"');
	    vecobj.push_back(Ngramobj(res[3],atol(res[1].c_str()),resstr));
	    keyword=res[0];
	}
    }
    ofs<<outputjson(keyword,vecobj)<<endl;
    return 0;
}
