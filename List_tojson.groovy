import groovy.json.*;



 // each node is represented with Nope
 class Nope{                                       

            public Nope root;
            public Nope child;
            public Nope sibling;
            public String val;                     //value of each node is a string 
            static int dep=0;                       //dep is a helper to reinitialize currentnode
            static Nope currentnode;                //node to keep a track of where to add the incoming node
            

             //constructor to initialize each node with its value i.e a string
            Nope(String var){                       
                  this.val=var;
            }


            // method to add each incoming node as a child or as a sibling to the currentnode.
            void add(String var){                       
                      Nope nodetoadd=new Nope(var);
                      if(root==null){               //if incoming node is the first incoming node then this block of if will execute
                              root=nodetoadd;
                      }

                            if(dep==0){               //dep is a helper to reinitialize currentnode 
                                  currentnode=root;
                            }

                        if(root!=null){
                                    Nope wheretoadd=currentnode;
                                    doadd(wheretoadd,nodetoadd);   //doadd is the major method to the addition of the incoming node

                          }
              }



//major method to form the tree structure in child-sibling relationship .It is called by add method.
// it adds the incoming node as a child or sibling to the appropriate node and updates the current node
// doesnot add if the incoming node is already being constructed in the tree.In this case it only updates the current node
            void doadd(Nope wheretoadd, Nope addnode){

                     if(wheretoadd.child==null )
                     {
                             if(wheretoadd.val==addnode.val){
                                     currentnode=wheretoadd;
                                     return;
                       }

                        wheretoadd.child=addnode;
                        currentnode=addnode;
                        return;

                        }
                        else{
                                if(wheretoadd.val==addnode.val){
                                        currentnode=wheretoadd;
                                        return;
                        }

                      }
                      while(true){  wheretoadd=wheretoadd.child;
                                    if(wheretoadd.sibling==null)
                                    {
                                       wheretoadd.sibling=addnode;
                                       currentnode=addnode;
                                       return;
                                    }
                                     else{
                                      while(wheretoadd.sibling!=null){
                                        Nope y=wheretoadd.sibling;
                                        if(y.val==addnode.val){
                                                currentnode=y;
                                                return;
                                        }
                                          wheretoadd=wheretoadd.sibling;

                                        }
                                        wheretoadd.sibling=addnode;
                                        currentnode=addnode;
                                        return;
                                    }
                           }

            }

//method which takes root of already constructed tree as object i.e roota 
//It also takes rootb as jsonobject which is to be constructed recursively 
//It takes map object which maps each node to its path
            JSONObject screenout(Nope roota,JSONObject rootb, Map mapp)
            {

                          Nope temp=roota;
                          if(roota==null)
                              return null;


                        def infos=new JSONArray();  //jsonarray to store all the child's information
                        // this block runs iteratively for each siblings in the same level
                          while(temp!=null)
                          {
                                  JSONObject info=new JSONObject();     //initializing json object for each sibling

                                  info.put("category",temp.val)         

                                  info.put("path",mapp[temp.val])



                                  roota=temp;
                                  temp=temp.sibling;                    //moving forward to next sibling node

                                  info.put("children",screenout(roota.child,info,mapp)); // recursively calling child of each sibling

                              infos.add(info);    // iteratively adding each sibling information to infos array

                          }

                          return infos;   //  returning the json object which consist of arrays of child to parent.
                      }



static void main(String[] args){

                def inp=["incident","incident/sapsystemusage","incident/securityauditlog","incident/gateway","incident/gateway/registeredservers"];

                //def inp=['compliance','compliance/sodconflicts','compliance/roleswithoneuser', 'compliance/criticalobject','compliance/criticalprofileuser', 'compliance/sodconflictobject',"compliance/sodconflictobject/Change purchase order","compliance/sodconflictobject/Release purchase order","compliance/sodconflictobject/Actual overhead calculation","compliance/sodconflictobject/CATS: Object-related approval","compliance/sodconflictobject/Change contract","compliance/sodconflictobject/Change General Ledger Master Data","compliance/sodconflictobject/Change or Release Purchase order","compliance/sodconflictobject/Change outline agreement","compliance/sodconflictobject/Change scheduling agreement","compliance/sodconflictobject/Change Vendor Master Data","compliance/sodconflictobject/Collective processing","compliance/sodconflictobject/Create a Goods Receipt","compliance/sodconflictobject/Create asset transactions","compliance/sodconflictobject/Create Contract","compliance/sodconflictobject/Create General Ledger Master Data","compliance/sodconflictobject/Create internal order","compliance/sodconflictobject/Create outline agreement","compliance/sodconflictobject/Create purchase order","compliance/sodconflictobject/Create scheduling agreement","compliance/sodconflictobject/Create vendor (Accounting)","compliance/sodconflictobject/Create Vendor Master Data","compliance/sodconflictobject/Create work breakdown structure","compliance/sodconflictobject/Current settings","compliance/sodconflictobject/Enrollment","compliance/sodconflictobject/Enter activity allocation","compliance/sodconflictobject/Enter reposting of primary costs","compliance/sodconflictobject/Exposure groups","compliance/sodconflictobject/IMG activity","compliance/sodconflictobject/Issue bank checks","compliance/sodconflictobject/Maintain payroll schemas","compliance/sodconflictobject/Maintain personnel control record","compliance/sodconflictobject/Maintain posting periods table","compliance/sodconflictobject/Maintain scheduling agreement schedule","compliance/sodconflictobject/Maintain times in time sheet","compliance/sodconflictobject/Mass activity: revenue distribution","compliance/sodconflictobject/Mass change","compliance/sodconflictobject/Mass change of purchase orders","compliance/sodconflictobject/Mass changes","compliance/sodconflictobject/Mass changing of contracts","compliance/sodconflictobject/Mass changing of scheduling agreements","compliance/sodconflictobject/Post with clearing","compliance/sodconflictobject/Post-capitalization","compliance/sodconflictobject/Price change: contracts","compliance/sodconflictobject/Price change: scheduling agreement","compliance/sodconflictobject/Process goods receipt for purchase order","compliance/sodconflictobject/Process Supplier Invoice in MM","compliance/sodconflictobject/Release purchase order","compliance/sodconflictobject/Reverse General Ledger account posting","compliance/sodconflictobject/Start additional expense posting","compliance/sodconflictobject/Time management pool"];
                //def inp=['configuration/forbiddenpwd', 'configuration/clientsettings','configuration/profileparameter', 'configuration/defaultuserpwd','configuration/tablelogging','configuration/devaccess','configuration/gateway','configuration/gateway/secinfo','configuration/gateway/reginfo']
                Iterator<String> ite = inp.iterator();


                def category_path_link=[:]; //empty map
                println(inp);
                println();

                // constructing lists of list from inp 
                //and constructing map of category to path simultaneously
                def inp_nestedlist=[];
                def temp1=[];
                def temp2=[];
                int sublist_size;
                while(ite.hasNext())
                {
                  temp1=ite.next();
                  temp1=temp1.split("/") as List;
                  sublist_size=temp1.size();
                  while(sublist_size!=0){
                      temp2=temp1.subList(0,sublist_size);
                      temp2=temp2.join('/');
                      category_path_link.put(temp1[sublist_size-1],temp2);
                      sublist_size--;
                  }

                  inp_nestedlist.add(temp1);
                }
                  println();
                // construction of nested list and map is finished




                Nope obj=new Nope();


                // block of nested for loops to construct the tree where the add method is called recursively
                //each time a string is passed from the list to the add method for which a node is created and contributed to the construction of child sibling tree 

                for(int i=0;i<inp_nestedlist.size();i++){
                    
                    Nope.dep=0;

                              for(int j=0;j<inp_nestedlist[i].size();j++){
                                  String to_store=inp_nestedlist[i][j].toString();
                                  obj.add(to_store);
                                  Nope.dep++;
                                }

                  }

                Nope rootie=obj.root;

              def rootj=new JsonBuilder();

              // screenout method to print in json format is called here.
             rootj.put("children",screenout(rootie,rootj,mapp));
            println(rootj.JSONString());

}

}
