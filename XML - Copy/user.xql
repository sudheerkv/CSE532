xquery version "3.0";
declare function local:name($urlid as xs:string)
{
    for $user in doc("/db/project3/user.xml")/LinkedOutDB/Userinfo
    where data($user/@URL)=$urlid
    return string($user/UserName)
};

declare function local:Query1(){
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,$user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData
    where
        ($user2/User = $user1/Endorses/UserTypeIDREF and
        $user2/User != $user1/User and
        $user1/Orgs[@Org=data($user2/Orgs/@Org)]/JoinDate < "2013-09-18" and 
        $user1/Orgs[@Org=data($user2/Orgs/@Org)]/EndDate > "2013-09-18" and
        $user2/Orgs[@Org=data($user1/Orgs/@Org)]/JoinDate < "2013-09-18" and 
        $user2/Orgs[@Org=data($user1/Orgs/@Org)]/EndDate > "2013-09-18")
    return (local:name(string($user1/User)),'endorses',local:name(string($user2/User)))
};

declare function local:Query2(){
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/Userinfo,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/Userinfo,
        $user in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $skill in doc("/db/project3/user.xml")/LinkedOutDB/Skillinfo
    let $url1 := data($user1/@URL),
        $url2 := data($user2/@URL),
        $skillid := data($skill/@SkillID)
    where
        ($url1!=$url2 and
         $user/User = $url1 and
         $user/Skills = $skillid and
         $user/Endorses/UserTypeIDREF = $url2 and
         $user/Endorses/SkillTypeIDREF = $skillid)
    return
        (
            for $user3 in doc("/db/project3/user.xml")/LinkedOutDB/UserData
            where 
                ($user3/User !=$url1 and
                 $user3/User !=$url2 and
                 $user3/Endorses/UserTypeIDREF = $url1 and
                 $user3/Endorses/UserTypeIDREF = $url2 and
                 $user/Endorses/SkillTypeIDREF = $skillid)
            return (string($user1/UserName),' endorses ',string($user2/UserName))
        )
};

declare function local:UserDoesnothaveSkill($skillid as xs:string)
{
    for $u in doc("/db/project3/user.xml")/LinkedOutDB/UserData 
    where every $skill in $u/Skills satisfies($skill!=$skillid)
    return $u/User
};

declare function local:Query3(){
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user in doc("/db/project3/user.xml")/LinkedOutDB/Userinfo,
        $skill in doc("/db/project3/user.xml")/LinkedOutDB/Skillinfo
    let $url := data($user/@URL),
        $skillid := data($skill/@SkillID),
        $u :=local:UserDoesnothaveSkill($skillid)
    where
        ($user1/User!=$user2/User and
         $user1/Endorses/UserTypeIDREF = $url and
         $user1/Endorses/SkillTypeIDREF = $skillid and
         $user2/Endorses/UserTypeIDREF = $url and
         $user2/Endorses/SkillTypeIDREF = $skillid)
    return $user[@URL=$u]
};

declare function local:userhasskill($skillid as xs:string, $urlid as xs:string)
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData 
    where (some $skill in $user1/Skills satisfies
    $skill = $skillid and $user1/User = $urlid)
    return $user1/User
};

declare function local:Query4()
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $skill in doc("/db/project3/user.xml")/LinkedOutDB/Skillinfo
    let $skillid := data($skill/@SkillID),
        $u :=local:UserDoesnothaveSkill($skillid)
    where
        (
            $user1/User!=$user2/User and 
            (every $skill in $user2/Skills satisfies 
            local:userhasskill($skill,$user1/User)=$user1/User) and
            $user1/Skills =$skillid
        )
    return 
        (distinct-values
            (for $u2 in $user2/User intersect $u,
                 $u1 in $user1/User
            return 
                (local:name($u1),' more skilled than ',local:name($u2))
            )
        )
};

declare function local:EachSkillEndorsed()
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData
    where every $skill in $user1/Skills satisfies
    (
        $user2/Endorses/SkillTypeIDREF = $skill and
        $user2/Endorses/UserTypeIDREF = $user1/User
    )
    return $user1/User
};

declare function local:Query5()
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $skill in doc("/db/project3/user.xml")/LinkedOutDB/Skillinfo
    let $skillid := data($skill/@SkillID),
        $u :=local:UserDoesnothaveSkill($skillid)
    where
        (
        $user1/User!=$user2/User and 
        (every $skill in $user2/Skills satisfies 
        local:userhasskill($skill,$user1/User)=$user1/User) and
        $user1/Skills =$skillid
        )
    return 
        (distinct-values
            (for $u2 in $user2/User intersect $u,
                 $u1 in $user1/User intersect local:EachSkillEndorsed()
            return 
                (local:name($u1),' more certified than ',local:name($u2), ' ')
            )
        )
};

declare function local:IndirectEndorseBase($url1 as xs:string, $url2 as xs:string)
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData
    where
        ($user1/User = $url1 and
         $user2/User = $url2 and
         $user1/Endorses/UserTypeIDREF = $user2/User and
         (every $url in $user2/Endorses/UserTypeIDREF satisfies 
            ($user1/User!=$url))
            )
    return $user2/User
};

declare function local:SkillDescendingBase($url1 as xs:string, $url2 as xs:string)
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $skill in doc("/db/project3/user.xml")/LinkedOutDB/Skillinfo
    let $skillid := data($skill/@SkillID),
        $u :=local:UserDoesnothaveSkill($skillid)
    where
        ($user1/User = $url1 and
         $user2/User = $url2 and
         $user1/Endorses/UserTypeIDREF = $user2/User and
         (every $url in $user2/Endorses/UserTypeIDREF satisfies ($user1/User!=$url)) and 
         (every $skill in $user2/Skills satisfies local:userhasskill($skill,$user1/User)=$user1/User) and
           $user1/Skills =$skillid
            )
    return $user2/User
};

declare function local:IndirectEndorse($url1 as xs:string, $url2 as xs:string, $no as xs:integer)
{
    distinct-values(for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user in doc("/db/project3/user.xml")/LinkedOutDB/UserData
    where
        ($user1/User = $url1 and
         $user2/User = $url2 and
         $user/User!= $url1 and $user/User!=$url2
            )
    return
        distinct-values(
            if($no=0) then(
                if(local:IndirectEndorseBase($url1,$url2) = $url2) then
                    (local:IndirectEndorse($url2,$user/User,0))
                else
                    ($url1)
                )
            else
            (
                if(local:SkillDescendingBase($url1,$url2) = $url2) then
                    (local:IndirectEndorse($url2,$user/User,1))
                else
                ($url1)
                )
            )
    )
};

declare function local:Query6()
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in $user1/Endorses/UserTypeIDREF
    return
    <result>
        {
    (for $u1 in $user1/User,
        $u2 in local:IndirectEndorse($user1/User,$user2,0)
        where $u1!=$u2
        return ($u1,$u2))
        }
    </result>
};

declare function local:Query7()
{
    for $user1 in doc("/db/project3/user.xml")/LinkedOutDB/UserData,
        $user2 in $user1/Endorses/UserTypeIDREF
    return
    <result>
        {
    (for $u1 in $user1/User,
        $u2 in local:IndirectEndorse($user1/User,$user2,1)
        where $u1!=$u2
        return ($u1,$u2))
        }
    </result>
};
local:Query7()